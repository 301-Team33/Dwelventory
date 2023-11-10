package com.example.dwelventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import com.example.dwelventory.R;
import com.example.dwelventory.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mainScenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testAddItem() {
        // add two items

        onView(withText("Total Cost")).check(matches(isDisplayed()));

        // Item item1 = new Item("Billy", date1, "Pygmy Goat", "Caramel w/ Black
        // Markings",serial,200, comment, photos);
        // Item item2 = new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black
        // Markings", 200);
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Jacob"));
        onView(withId(R.id.date_button)).perform(ViewActions.typeText("1-2-3456"));
        onView(withId(R.id.make_button)).perform(ViewActions.typeText("Pygmy Goat"));
        onView(withId(R.id.model_button)).perform(ViewActions.typeText("Caramel w/ Black Markings"));
        Espresso.pressBack();
        onView(withId(R.id.estimated_val_button)).perform(ViewActions.typeText("200"));
        Espresso.pressBack();
        onView(withText("Confirm")).check(matches(isDisplayed()));
        onView(withText("Confirm")).perform(click());
        onView(withText("Total Cost"));
        // asserts that item was added
        onView(withText("Jacob")).check(matches(isDisplayed()));
        //
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Billy"));
        onView(withId(R.id.date_button)).perform(ViewActions.typeText("10-21-9876"));
        onView(withId(R.id.make_button)).perform(ViewActions.typeText("Pygmy Goat"));
        onView(withId(R.id.model_button)).perform(ViewActions.typeText("Caramel w/ Black Markings"));

        Espresso.pressBack();
        onView(withId(R.id.estimated_val_button)).perform(ViewActions.typeText("200"));
        Espresso.pressBack();
        onView(withText("Confirm")).perform(click());
    }

    @Test
    public void testMakeFilter() {
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Tom"));
        onView(withId(R.id.date_button)).perform(ViewActions.typeText("03-21-2050"));
        onView(withId(R.id.make_button)).perform(ViewActions.typeText("Poodle"));
        onView(withId(R.id.model_button)).perform(ViewActions.typeText("Black with brown eyes"));
        Espresso.pressBack();
        onView(withId(R.id.estimated_val_button)).perform(ViewActions.typeText("35"));
        Espresso.pressBack();

        onView(withText("Confirm")).perform(click());
        onView(withText("Total Cost")).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectionMode() {
        onData(anything())
                .inAdapterView(withId(R.id.item_list))
                .atPosition(0)
                .perform(longClick());

        onView(withText("Selected Items : 0")).check(matches(isDisplayed()));

        onView(withId(R.id.selectAll)).perform(click());

        onView(withText("Selected Items : 2")).check(matches(isDisplayed()));

        onView(withId(R.id.deletebtn)).perform(click());

        onView(withText("Selected Items : 0")).check(matches(isDisplayed()));

        onView(withId(R.id.closebtn)).perform(click());

    }
}
