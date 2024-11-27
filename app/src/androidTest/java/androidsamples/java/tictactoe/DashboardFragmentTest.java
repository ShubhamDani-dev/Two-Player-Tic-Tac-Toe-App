package androidsamples.java.tictactoe;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DashboardFragmentTest {

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        clearSharedPreferences();
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }


    @Test
    public void testNewGameButtonShowsDialog() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.fab_new_game)).perform(click());
            onView(withText(R.string.new_game)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testNavigationToSinglePlayerGame() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.fab_new_game)).perform(click());
            onView(withText(R.string.one_player)).perform(click());
        }
    }

    @Test
    public void testNavigationToTwoPlayerGame() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R .id.fab_new_game)).perform(click());
            onView(withText(R.string.two_player)).perform(click());
        }
    }

    private void clearSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences("GameStats", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}