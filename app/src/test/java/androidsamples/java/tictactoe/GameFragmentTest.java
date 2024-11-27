package androidsamples.java.tictactoe;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameFragmentTest {

    private GameFragment gameFragment;

    @Before
    public void setUp() {
        gameFragment = new GameFragment();
        gameFragment.board = new String[9]; // Initialize board with 9 empty spots
    }

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
    public void testCheckDraw() {
        // Simulate a draw scenario (board is full with no winner)
        gameFragment.board = new String[]{"X", "O", "X", "X", "O", "X", "O", "X", "O"};

        // Check if the game detects a draw
        boolean result = gameFragment.isBoardFull();

        // Assert that the board is full
        assertTrue(result);
    }

    @Test
    public void testIsBoardFull_withEmptyCells() {
        // Simulate an incomplete board (with empty cells)
        gameFragment.board = new String[]{"X", "O", "X", null, "O", null, null, null, null};

        // Check if the board is full
        boolean result = gameFragment.isBoardFull();

        // Assert that the board is not full
        assertFalse(result);
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
    public void testCheckWin_diagonal() {
        // Simulate a diagonal win scenario (top-left to bottom-right)
        gameFragment.board = new String[]{"X", "O", null, "O", "X", null, null, null, "X"};

        // Check for a win
        boolean result = gameFragment.checkWin("X");

        // Assert that the player "X" wins
        assertTrue(result);
    }

    @Test
    public void testIsBoardFull_notFull() {
        // Simulate a board that is not full
        gameFragment.board = new String[]{"X", "O", "X", "O", null, null, "X", "O", null};

        // Check if the board is full
        boolean result = gameFragment.isBoardFull();

        // Assert that the board is not full
        assertFalse(result);
    }
    
}