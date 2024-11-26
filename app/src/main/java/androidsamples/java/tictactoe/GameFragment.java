package androidsamples.java.tictactoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.Objects;

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
  private ValueEventListener gameListener;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // Extract game type and optional game ID
    GameFragmentArgs args = GameFragmentArgs.fromBundle(requireArguments());
    String gameType = args.getGameType();
    gameId = args.getGameId();

    isTwoPlayerMode = gameType.equals(getString(R.string.two_player));
    if (isTwoPlayerMode && gameId != null) {
      FirebaseDatabase database = FirebaseDatabase.getInstance("https://sdpd-tictactoe-default-rtdb.asia-southeast1.firebasedatabase.app/");
      Log.d(TAG, "Initialized Firebase with URL: " + database.getApp().getOptions().getDatabaseUrl());
      gameRef = database.getReference("games").child(gameId);
      Log.d(TAG, "Game ID = " + gameId);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_game, container, false);

    // Display the game ID if in two-player mode
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

    if (isTwoPlayerMode && gameRef != null) {
      // Attach listener to Firebase
      gameListener = gameRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          // Retrieve game state
          if (snapshot.exists()) {
            Log.d(TAG, "Firebase data changed: " + snapshot.getValue());

            // Update board
            for (int i = 0; i < GRID_SIZE; i++) {
              String cell = snapshot.child("board").child(String.valueOf(i)).getValue(String.class);
              board[i] = cell;
              if (cell != null) {
                mButtons[i].setText(cell);
              } else {
                mButtons[i].setText("");
              }
            }

            // Update turn
            String currentTurn = snapshot.child("turn").getValue(String.class);
            if (currentTurn != null) {
              // Check if it's the local player's turn
              isPlayerTurn = currentTurn.equals(playerSymbol);
              Log.d(TAG, "Current turn: " + currentTurn);
            }
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e(TAG, "Firebase listener cancelled: " + error.getMessage());
        }
      });
    }
  }

  private void initializeButtons(View view) {
    for (int i = 0; i < GRID_SIZE; i++) {
      mButtons[i] = view.findViewById(getResources().getIdentifier("button" + i, "id", requireActivity().getPackageName()));
      int finalI = i;
      mButtons[i].setOnClickListener(v -> handlePlayerMove(finalI));
    }
  }

  private void handlePlayerMove(int index) {
    Log.d(TAG, "Handling player move. Index: " + index + ", isPlayerTurn: " + isPlayerTurn);

    if (!isPlayerTurn || isGameOver || board[index] != null) {
      Log.d(TAG, "Move not allowed: isPlayerTurn=" + isPlayerTurn + ", isGameOver=" + isGameOver + ", board[index]=" + board[index]);
      return;
    }

    // Player makes a move
    board[index] = playerSymbol;
    mButtons[index].setText(playerSymbol);
    isPlayerTurn = false;

    if (isTwoPlayerMode) {
      // Update Firebase for two-player mode
      if (gameRef != null) {
        gameRef.child("board").child(String.valueOf(index)).setValue(playerSymbol);
        gameRef.child("turn").setValue(opponentSymbol);
      }
    }

    // Check if the player won or the game is a draw
    if (checkGameOutcome(playerSymbol, getString(R.string.player_wins))) return;
  }

  private boolean checkGameOutcome(String player, String winMessage) {
    if (checkWin(player)) {
      showGameOverDialog(winMessage);
      return true;
    }
    if (isBoardFull()) {
      showGameOverDialog(getString(R.string.game_draw));
      return true;
    }
    return false;
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
    isGameOver = true;
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
                  .setPositiveButton(R.string.yes, (dialog, which) -> mNavController.popBackStack())
                  .setNegativeButton(R.string.no, null)
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
    if (isTwoPlayerMode && gameRef != null && gameListener != null) {
      gameRef.removeEventListener(gameListener);
    }
  }
}