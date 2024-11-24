package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class GameFragment extends Fragment {
  private static final String TAG = "GameFragment";
  private static final int GRID_SIZE = 9;

  private final Button[] mButtons = new Button[GRID_SIZE];
  private final String[] board = new String[GRID_SIZE];
  private boolean isPlayerTurn = true; // Player goes first
  private boolean isGameOver = false;
  private NavController mNavController;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    // Extract game type
    GameFragmentArgs args = GameFragmentArgs.fromBundle(getArguments());
    String gameType = args.getGameType();
    Log.d(TAG, "Game type = " + gameType);
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_game, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    initializeButtons(view);
  }

  private void initializeButtons(View view) {
    for (int i = 0; i < GRID_SIZE; i++) {
      mButtons[i] = view.findViewById(getResources().getIdentifier("button" + i, "id", requireActivity().getPackageName()));
      int finalI = i;
      mButtons[i].setOnClickListener(v -> handlePlayerMove(finalI));
    }
  }

  private void handlePlayerMove(int index) {
    if (!isPlayerTurn || isGameOver || board[index] != null) {
      return;
    }

    // Player makes a move
    board[index] = "X";
    mButtons[index].setText("X");
    isPlayerTurn = false;

    // Check if the player won or the game is a draw
    if (checkGameOutcome()) return;

    // Bot makes a move
    handleBotMove();
  }

  private void handleBotMove() {
    if (isGameOver) return;

    // Find a random empty cell
    int botMove = findRandomEmptyCell();
    if (botMove != -1) {
      board[botMove] = "O";
      mButtons[botMove].setText("O");
    }

    // Check if the bot won or the game is a draw
    if (checkGameOutcome()) return;

    // Return control to the player
    isPlayerTurn = true;
  }

  private int findRandomEmptyCell() {
    for (int i = 0; i < GRID_SIZE; i++) {
      if (board[i] == null) {
        return i;
      }
    }
    return -1;
  }

  private boolean checkGameOutcome() {
    // Check rows, columns, diagonals for a win
    if (checkWin("X")) {
      showGameOverDialog(getString(R.string.player_wins));
      return true;
    } else if (checkWin("O")) {
      showGameOverDialog(getString(R.string.bot_wins));
      return true;
    }

    // Check for a draw
    if (isBoardFull()) {
      showGameOverDialog(getString(R.string.game_draw));
      return true;
    }

    return false;
  }

  private boolean checkWin(String player) {
    int[][] winConditions = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // Rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // Columns
            {0, 4, 8}, {2, 4, 6}             // Diagonals
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
}