package androidsamples.java.tictactoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  private final String[] board = new String[GRID_SIZE];
  private boolean isPlayerTurn = true;
  private boolean isGameOver = false;
  private boolean isTwoPlayerMode = false;

  private String playerSymbol = "X";
  private String opponentSymbol = "O";
  private String gameId;
  private DatabaseReference gameRef;

  private NavController mNavController;
  private ValueEventListener gameStateListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    GameFragmentArgs args = GameFragmentArgs.fromBundle(requireArguments());
    String gameType = args.getGameType();
    gameId = args.getGameId();

    isTwoPlayerMode = gameType.equals(getString(R.string.two_player));
    if (isTwoPlayerMode && gameId != null) {
      FirebaseDatabase database = FirebaseDatabase.getInstance("https://sdpd-tictactoe-default-rtdb.asia-southeast1.firebasedatabase.app/");
      gameRef = database.getReference("games").child(gameId);

      // Check if the game room is new
      gameRef.child("creator").get().addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
          DataSnapshot snapshot = task.getResult();
          if (!snapshot.exists()) {
            // Player is the creator
            initializeGameInFirebase();
            playerSymbol = "X";
            opponentSymbol = "O";
            gameRef.child("creator").setValue(getPlayerId());
          } else {
            // Player is joining as opponent
            playerSymbol = "O";
            opponentSymbol = "X";
          }
        }
      });
    }
  }

  private String getPlayerId() {
    // Generate a unique identifier for the player
    // This could be a Firebase Authentication UID or a randomly generated UUID
    return "player_" + System.currentTimeMillis();
  }

  private void initializeGameInFirebase() {
    for (int i = 0; i < GRID_SIZE; i++) {
      gameRef.child("board").child(String.valueOf(i)).setValue(null);
    }
    gameRef.child("turn").setValue("X"); // X always starts
    gameRef.child("status").setValue("active");
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_game, container, false);

    TextView gameIdTextView = view.findViewById(R.id.text_game_id);
    if (isTwoPlayerMode && gameIdTextView != null) {
      if (gameId != null && !gameId.isEmpty()) {
        gameIdTextView.setText(getString(R.string.game_id_display, gameId));
        gameIdTextView.setVisibility(View.VISIBLE);
      } else {
        gameIdTextView.setVisibility(View.GONE);
      }
    }

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    initializeButtons(view);
    setupBackPressCallback();
    setupGameStateListener();
  }

  private void initializeButtons(View view) {
    for (int i = 0; i < GRID_SIZE; i++) {
      mButtons[i] = view.findViewById(getResources().getIdentifier("button" + i, "id", requireActivity().getPackageName()));
      int finalI = i;
      mButtons[i].setOnClickListener(v -> handlePlayerMove(finalI));
    }
  }

  private void setupGameStateListener() {
    if (isTwoPlayerMode && gameRef != null) {
      gameStateListener = gameRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          if (snapshot.exists()) {
            updateBoardFromFirebase(snapshot);
            updateTurnStatus(snapshot);
            checkGameStatus(snapshot);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e(TAG, "Game state listener error: " + error.getMessage());
        }
      });
    }
  }

  private void updateBoardFromFirebase(DataSnapshot snapshot) {
    for (int i = 0; i < GRID_SIZE; i++) {
      String cell = snapshot.child("board").child(String.valueOf(i)).getValue(String.class);
      board[i] = cell;
      if (cell != null) {
        mButtons[i].setText(cell);
        mButtons[i].setEnabled(false);
      } else {
        mButtons[i].setText("");
        mButtons[i].setEnabled(true);
      }
    }
  }

  private void updateTurnStatus(DataSnapshot snapshot) {
    String currentTurn = snapshot.child("turn").getValue(String.class);
    isPlayerTurn = playerSymbol.equals(currentTurn);

    TextView waitingTextView = getView().findViewById(R.id.text_waiting_for_opponent);
    if (waitingTextView != null) {
      waitingTextView.setVisibility(isPlayerTurn ? View.GONE : View.VISIBLE);
    }
  }

  private void checkGameStatus(DataSnapshot snapshot) {
    String gameStatus = snapshot.child("status").getValue(String.class);
    if ("completed".equals(gameStatus)) {
      String winner = snapshot.child("winner").getValue(String.class);
      handleGameCompletion(winner);
    }
  }

  private void handleGameCompletion(String winner) {
    if (winner == null) {
      showGameOverDialog(getString(R.string.game_draw));
    } else if (winner.equals(playerSymbol)) {
      showGameOverDialog(getString(R.string.player_wins));
    } else {
      showGameOverDialog(getString(R.string.opponent_wins));
    }
    isGameOver = true;
  }

  private void handlePlayerMove(int index) {
    if (!isPlayerTurn || isGameOver || board[index] != null) {
      return;
    }

    performMove(index, playerSymbol);
  }

  private void performMove(int index, String symbol) {
    board[index] = symbol;
    mButtons[index].setText(symbol);
    mButtons[index].setEnabled(false);

    if (isTwoPlayerMode && gameRef != null) {
      updateGameState(index, symbol);

      // Check game outcome
      if (checkWin(symbol)) {
        gameRef.child("winner").setValue(symbol);
        gameRef.child("status").setValue("completed");
      } else if (isBoardFull()) {
//        gameRef.child("winner").setValue(null);
        gameRef.child("status").setValue("completed");
      }
    }
  }

  private void updateGameState(int index, String symbol) {
    gameRef.child("board").child(String.valueOf(index)).setValue(symbol);
    gameRef.child("turn").setValue(symbol.equals(playerSymbol) ? opponentSymbol : playerSymbol);
    isPlayerTurn = !symbol.equals(playerSymbol);
  }

  private boolean checkWin(String player) {
    int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };
    for (int[] condition : winConditions) {
      if (player.equals(board[condition[0]]) &&
              player.equals(board[condition[1]]) &&
              player.equals(board[condition[2]])) {
        return true;
      }
    }
    return false;
  }

  private boolean isBoardFull() {
    for (String cell : board) {
      if (cell == null) return false;
    }
    return true;
  }

  private void showGameOverDialog(String message) {
    new AlertDialog.Builder(requireActivity())
            .setTitle(R.string.game_over)
            .setMessage(message)
            .setPositiveButton(R.string.ok, (dialog, which) -> mNavController.popBackStack())
            .setCancelable(false)
            .create()
            .show();
  }

  private void setupBackPressCallback() {
    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        if (!isGameOver) {
          new AlertDialog.Builder(requireActivity())
                  .setTitle(R.string.confirm_exit)
                  .setMessage(R.string.forfeit_message)
                  .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (isTwoPlayerMode && gameRef != null) {
                      gameRef.child("status").setValue("abandoned");
                    }
                    mNavController.popBackStack();
                  })
                  .setNegativeButton(R.string.no, null)
                  .setCancelable(false)
                  .create()
                  .show();
        } else {
          mNavController.popBackStack();
        }
      }
    });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    if (gameStateListener != null && gameRef != null) {
      gameRef.removeEventListener(gameStateListener);
    }
  }
}