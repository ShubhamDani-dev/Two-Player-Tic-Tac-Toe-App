package androidsamples.java.tictactoe;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameFragmentTest {

    private GameFragment gameFragment;

    @Before
    public void setUp() {
        gameFragment = new GameFragment();
        gameFragment.board = new String[9]; // Initialize board with 9 empty spots
    }

    /* --------------- Win Condition Tests --------------- */

    @Test
    public void testCheckWin_vertical() {
        // Simulate a vertical win scenario (first column)
        gameFragment.board = new String[]{"X", "O", null, "X", "O", null, "X", null, null};

        // Check for a win
        boolean result = gameFragment.checkWin("X");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_verticalO() {
        // Simulate a vertical win scenario (first column)
        gameFragment.board = new String[]{"X", "O", null, "X", "O", null, null, "O", null};

        // Check for a win
        boolean result = gameFragment.checkWin("O");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_horizontal() {
        // Simulate a horizontal win scenario (first row)
        gameFragment.board = new String[]{"X", "X", "X", "O", "O", null, null, null, null};

        // Check for a win
        boolean result = gameFragment.checkWin("X");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_horizontalO() {
        // Simulate a horizontal win scenario (first row)
        gameFragment.board = new String[]{"X", null , "X", "O", "O", "O", null, null, null};

        // Check for a win
        boolean result = gameFragment.checkWin("O");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_diagonal_topLeftToBottomRight() {
        // Simulate a diagonal win scenario (top-left to bottom-right)
        gameFragment.board = new String[]{"X", "O", null, "O", "X", null, null, null, "X"};

        // Check for a win
        boolean result = gameFragment.checkWin("X");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_diagonal_topLeftToBottomRightO() {
        // Simulate a diagonal win scenario (top-left to bottom-right)
        gameFragment.board = new String[]{"O", "X", null, "X", "O", null, "X", null, "O"};

        // Check for a win
        boolean result = gameFragment.checkWin("O");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_diagonal_topRightToBottomLeft() {
        // Simulate a diagonal win scenario (top-right to bottom-left)
        gameFragment.board = new String[]{null, null, "O", "X", "O", null, "O", null, "X"};

        // Check for a win
        boolean result = gameFragment.checkWin("O");

        // Assert that the player "O" wins
        assertTrue(result);
    }

    @Test
    public void testCheckWin_noWinner() {
        // Simulate a scenario where there is no winner
        gameFragment.board = new String[]{"X", "O", "X", "O", "X", "O", "O", "X", "O"};

        // Check for a win
        boolean result = gameFragment.checkWin("X");

        // Assert that no win is detected
        assertFalse(result);
    }

    /* --------------- Board State Tests --------------- */

    @Test
    public void testIsBoardFull_fullBoard() {
        // Simulate a full board
        gameFragment.board = new String[]{"X", "O", "X", "O", "X", "O", "X", "O", "X"};

        // Check if the board is full
        boolean result = gameFragment.isBoardFull();

        // Assert that the board is full
        assertTrue(result);
    }

    @Test
    public void testIsBoardFull_withEmptyCells() {
        // Simulate an incomplete board
        gameFragment.board = new String[]{"X", "O", "X", null, "O", null, "X", null, null};

        // Check if the board is full
        boolean result = gameFragment.isBoardFull();

        // Assert that the board is not full
        assertFalse(result);
    }



}
