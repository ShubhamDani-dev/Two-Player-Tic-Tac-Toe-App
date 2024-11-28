package androidsamples.java.tictactoe;

import android.widget.Button;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup() {
        // Set up the environment before the test
    }

    @Test
    public void testSinglePlayerMode() {
        // Click on the "+" button to start a new game
        Espresso.onView(ViewMatchers.withId(R.id.fab_new_game))
                .perform(ViewActions.click());

        // Select "One-Player" option
        Espresso.onView(ViewMatchers.withText("One-Player"))
                .perform(ViewActions.click());


        // Check if user can make a move by clicking a button (button0 for example)
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .perform(ViewActions.click());

    }


    @Test
    public void testTwoPlayerMode() {
        // Click on the "+" button to start a new game
        Espresso.onView(ViewMatchers.withId(R.id.fab_new_game))
                .perform(ViewActions.click());

        // Select "Two-Player" option
        Espresso.onView(ViewMatchers.withText("Two-Player"))
                .perform(ViewActions.click());

        // Enter a valid room ID
        Espresso.onView(ViewMatchers.withId(R.id.edit_game_id))
                .perform(ViewActions.typeText("12345"));

        // Click "OK" to start the game
        Espresso.onView(ViewMatchers.withText("START GAME"))
                .perform(ViewActions.click());

    }


    @Test
    public void testTwoPlayerForfeit() {
        // Click on the "+" button to start a new game
        Espresso.onView(ViewMatchers.withId(R.id.fab_new_game))
                .perform(ViewActions.click());

        // Select "Two-Player" option
        Espresso.onView(ViewMatchers.withText("Two-Player"))
                .perform(ViewActions.click());

        // Enter a valid room ID
        Espresso.onView(ViewMatchers.withId(R.id.edit_game_id))
                .perform(ViewActions.typeText("12345"));

        // Click "OK" to start the game
        Espresso.onView(ViewMatchers.withText("START GAME"))
                .perform(ViewActions.click());

        // Check if user can make a move by clicking a button (button0 for example)
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .perform(ViewActions.click());

        // Press the back button
        Espresso.pressBack();

        // Click on the "FORFEIT" button
        Espresso.onView(ViewMatchers.withText("FORFEIT!"))
                .perform(ViewActions.click());

    }

    @Test
    public void testGameButtonClicks() {
        // Start a game (one-player or two-player)
        testSinglePlayerMode();  // You can replace this with testStartTwoPlayerGame()

        // Simulate clicking on a game button
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .perform(ViewActions.click());

        // Verify that the button has been clicked and its text changes (for example, "X")
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .check(ViewAssertions.matches(ViewMatchers.withText("X")));
    }

    @Test
    public void testforfeit() {
        // Click on the "+" button to start a new game
        Espresso.onView(ViewMatchers.withId(R.id.fab_new_game))
                .perform(ViewActions.click());

        // Select "One-Player" option
        Espresso.onView(ViewMatchers.withText("One-Player"))
                .perform(ViewActions.click());

        // Check if user can make a move by clicking a button (button0 for example)
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .perform(ViewActions.click());

        // Press the back button
        Espresso.pressBack();

        // Click on the "FORFEIT" button
        Espresso.onView(ViewMatchers.withText("FORFEIT!"))
                .perform(ViewActions.click());
    }

    @Test
    public void testforfeitcancel() {
        // Click on the "+" button to start a new game
        Espresso.onView(ViewMatchers.withId(R.id.fab_new_game))
                .perform(ViewActions.click());

        // Select "One-Player" option
        Espresso.onView(ViewMatchers.withText("One-Player"))
                .perform(ViewActions.click());

        // Check if user can make a move by clicking a button (button0 for example)
        Espresso.onView(ViewMatchers.withId(R.id.button0))
                .perform(ViewActions.click());

        // Press the back button
        Espresso.pressBack();

        // Click on the "FORFEIT" button
        Espresso.onView(ViewMatchers.withText("CANCEL"))
                .perform(ViewActions.click());
    }




    @After
    public void tearDown() {
        // Clean up after the test
    }
}
