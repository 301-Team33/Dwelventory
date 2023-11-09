package com.example.dwelventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;



public class TagFragmentTests {

    @Test
    public void testTagFragmentNavigationOpen(){
        // click the new item button
        onView(withId(R.id.add_item_button)).perform(click());
        // check that we navigated to the activity
        onView(withText("Edit Tag")).check(matches(isDisplayed()));

        onView(withId(R.id.edit_tag_button)).perform(click());

        onView(withText("TAGS")).check(matches(isDisplayed()));
    }

    @Test
    public void testAddingTags(){
        // Navigate to the fragment
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.edit_tag_button)).perform(click());

        onView(withId(R.id.tag_edittext)).perform(ViewActions.typeText("Kitchen"));
        onView(withId(R.id.tag_create_button)).perform(click());

        // Check if the tag was added!
        onView(withText("Kitchen")).check(matches(isDisplayed()));
    }
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
}
