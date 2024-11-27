package androidsamples.java.tictactoe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  String[] board = new String[GRID_SIZE];
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
    initializeGameParameters();
    setupFirebaseForTwoPlayerMode();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_game, container, false);
    displayGameIdIfApplicable(view);
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

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    removeGameStateListener();
  }

  /* ----------------- Initialization Methods ----------------- */

  private void initializeGameParameters() {
    GameFragmentArgs args = GameFragmentArgs.fromBundle(requireArguments());
    String gameType = args.getGameType();
    gameId = args.getGameId();
    isTwoPlayerMode = gameType.equals(getString(R.string.two_player));
  }

  private void setupFirebaseForTwoPlayerMode() {
    if (isTwoPlayerMode && gameId != null) {
      FirebaseDatabase database = FirebaseDatabase.getInstance("https://sdpd-tictactoe-default-rtdb.asia-southeast1.firebasedatabase.app/");
      gameRef = database.getReference("games").child(gameId);
      gameRef.child("creator").get().addOnCompleteListener(task -> configurePlayerRoles(task.getResult()));
    }
  }

  private void configurePlayerRoles(DataSnapshot snapshot) {
    if (snapshot != null && !snapshot.exists()) {
      initializeGameInFirebase();
      playerSymbol = "X";
      opponentSymbol = "O";
      gameRef.child("creator").setValue(getPlayerId());
    } else {
      playerSymbol = "O";
      opponentSymbol = "X";
    }
  }

  private void initializeGameInFirebase() {
    for (int i = 0; i < GRID_SIZE; i++) {
      gameRef.child("board").child(String.valueOf(i)).setValue(null);
    }
    gameRef.child("turn").setValue("X");
    gameRef.child("status").setValue("active");
  }

  private String getPlayerId() {
    return "player_" + System.currentTimeMillis();
  }

  /* ----------------- UI Methods ----------------- */

  private void displayGameIdIfApplicable(View view) {
    TextView gameIdTextView = view.findViewById(R.id.text_game_id);
    if (isTwoPlayerMode && gameIdTextView != null) {
      if (gameId != null && !gameId.isEmpty()) {
        gameIdTextView.setText(getString(R.string.game_id_display, gameId));
        gameIdTextView.setVisibility(View.VISIBLE);
      } else {
        gameIdTextView.setVisibility(View.GONE);
      }
    }
  }

  private void initializeButtons(View view) {
    for (int i = 0; i < GRID_SIZE; i++) {
      mButtons[i] = view.findViewById(getResources().getIdentifier("button" + i, "id", requireActivity().getPackageName()));
      int finalI = i;
      mButtons[i].setOnClickListener(v -> handlePlayerMove(finalI));
    }
  }

  /* ----------------- Game Logic Methods ----------------- */

  private void handlePlayerMove(int index) {
    if (!isPlayerTurn || isGameOver || board[index] != null) {
      return; // Ignore invalid moves
    }

    // Perform the player's move
    performMove(index, playerSymbol);

    // Check if the player wins after their move
    if (checkWin(playerSymbol)) {
      updateStats(1,0,0);
      showGameOverDialog(getString(R.string.player_wins));
      isGameOver = true;
    } else if (isBoardFull()) {
      updateStats(0,0,1);
      showGameOverDialog(getString(R.string.game_draw));
      isGameOver = true;
    } else {
      // Allow AI to make a move if it's single-player mode
      if (!isTwoPlayerMode) {
        performAiMove();
      }
    }
  }

  void performMove(int index, String symbol) {
    updateBoard(index, symbol);
    if (isTwoPlayerMode) {
      updateGameStateInFirebase(index, symbol);
    }
  }

  private void performAiMove() {
    if (isGameOver || isTwoPlayerMode) {
      // AI does not move if the game is over or in two-player mode
      return;
    }

    // Find a random empty cell for AI to move
    List<Integer> availableMoves = getAvailableMoves();
    if (availableMoves.isEmpty()) return; // No valid moves

    int aiMove = availableMoves.get(new Random().nextInt(availableMoves.size()));

    // Perform AI's move
    performMove(aiMove, opponentSymbol);

    // Check game outcome after AI move
    if (checkWin(opponentSymbol)) {
      updateStats(0, 1, 0);
      showGameOverDialog(getString(R.string.opponent_wins));
      isGameOver = true;
    } else if (isBoardFull()) {
      updateStats(0, 0, 1);
      showGameOverDialog(getString(R.string.game_draw));
      isGameOver = true;
    }
  }

  private List<Integer> getAvailableMoves() {
    List<Integer> availableMoves = new ArrayList<>();
    for (int i = 0; i < GRID_SIZE; i++) {
      if (board[i] == null) {
        availableMoves.add(i);
      }
    }
    return availableMoves;
  }

  private void updateBoard(int index, String symbol) {
    board[index] = symbol;
    mButtons[index].setText(symbol);
    mButtons[index].setEnabled(false);
  }

  private void updateGameStateInFirebase(int index, String symbol) {
    gameRef.child("board").child(String.valueOf(index)).setValue(symbol);
    gameRef.child("turn").setValue(symbol.equals(playerSymbol) ? opponentSymbol : playerSymbol);
    isPlayerTurn = !symbol.equals(playerSymbol);

    if (checkWin(symbol)) {
      gameRef.child("winner").setValue(symbol);
      gameRef.child("status").setValue("completed");
    } else if (isBoardFull()) {
      gameRef.child("status").setValue("completed");
    }
  }

  boolean checkWin(String player) {
    int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
    };

    for (int[] condition : winConditions) {
      if (player.equals(board[condition[0]]) &&
              player.equals(board[condition[1]]) &&
              player.equals(board[condition[2]])) {
        return true; // Player wins
      }
    }
    return false;
  }

  boolean isBoardFull() {
    for (String cell : board) {
      if (cell == null) {
        return false; // At least one empty cell
      }
    }
    return true; // No empty cells
  }

  /* ----------------- Firebase State Handling ----------------- */

  private void setupGameStateListener() {
    if (isTwoPlayerMode && gameRef != null) {
      gameStateListener = gameRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
          if (snapshot.exists()) {
            updateGameStateFromFirebase(snapshot);
          }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
          Log.e(TAG, "Game state listener error: " + error.getMessage());
        }
      });
    }
  }

  private void updateGameStateFromFirebase(DataSnapshot snapshot) {
    updateBoardFromFirebase(snapshot);
    updateTurnStatus(snapshot);
    checkGameCompletionStatus(snapshot);
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

  private void checkGameCompletionStatus(DataSnapshot snapshot) {
    String winner = snapshot.child("winner").getValue(String.class);
    String status = snapshot.child("status").getValue(String.class);

    if ("completed".equals(status) && !isGameOver) {
      isGameOver = true;
      handleGameCompletion(winner);
    }
  }

  private void removeGameStateListener() {
    if (isTwoPlayerMode && gameRef != null && gameStateListener != null) {
      gameRef.removeEventListener(gameStateListener);
    }
  }

  /* ----------------- Game Completion ----------------- */

  private void handleGameCompletion(String winner) {
    if (winner == null) {
      // Draw
      updateStats(0, 0, 1);
      showGameOverDialog(getString(R.string.game_draw));
    } else if (winner.equals(playerSymbol)) {
      // Player wins
      updateStats(1, 0, 0); // Increment wins
      showGameOverDialog(getString(R.string.player_wins));
    } else {
      // Opponent wins
      updateStats(0, 1, 0); // Increment losses
      showGameOverDialog(getString(R.string.opponent_wins));
    }
  }

  private void updateStats(int winIncrement, int lossIncrement, int drawIncrement) {
    SharedPreferences prefs = requireContext().getSharedPreferences("GameStats", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = prefs.edit();

    // Fetch existing stats
    int currentWins = prefs.getInt("wins", 0);
    int currentLosses = prefs.getInt("losses", 0);
    int currentDraws = prefs.getInt("draws", 0);

    // Update stats
    editor.putInt("wins", currentWins + winIncrement);
    editor.putInt("losses", currentLosses + lossIncrement);
    editor.putInt("draws", currentDraws + drawIncrement);

    // Commit changes
    editor.apply();

    // Log updated stats
    Log.d(TAG, "Updated stats - Wins: " + (currentWins + winIncrement) +
            ", Losses: " + (currentLosses + lossIncrement) +
            ", Draws: " + (currentDraws + drawIncrement));
  }

  /* ----------------- Navigation and Dialogs ----------------- */

  private void setupBackPressCallback() {
    requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
      @Override
      public void handleOnBackPressed() {
        if (!isGameOver) {
          showForfeitConfirmationDialog();
        } else {
          navigateToDashboard();
        }
      }
    });
  }

  private void showForfeitConfirmationDialog() {
    new AlertDialog.Builder(requireContext())
            .setMessage(R.string.confirm_forfeit)
            .setPositiveButton(R.string.forfeit, (dialog, which) -> {
              if (isTwoPlayerMode) gameRef.child("winner").setValue(opponentSymbol);
              navigateToDashboard();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
  }

  private void showGameOverDialog(String message) {
    new AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(R.string.ok, (dialog, which) -> {
              navigateToDashboard();
            })
            .show();
  }

  private void navigateToDashboard() {
    mNavController.navigate(R.id.dashboardFragment);
  }
}