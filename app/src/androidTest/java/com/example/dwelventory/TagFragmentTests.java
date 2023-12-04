package com.example.dwelventory;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;



public class TagFragmentTests {

    @After
    public void tearDown() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Test
    public void testTagFragmentNavigationOpen(){
        // click the new item button
        onView(withId(R.id.add_item_button)).perform(click());
        // check that we navigated to the activity
        //onView(withText("Edit Tag")).check(matches(isDisplayed()));

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
    @Test
    public void testSameAddedTag(){
        // navigate to the fragment
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.edit_tag_button)).perform(click());
        // Check to see if the same tag with different capitalization can be added
        onView(withId(R.id.tag_edittext)).perform(ViewActions.typeText("Kitchen"));
        onView(withId(R.id.tag_create_button)).perform(click());
        onView(withText("Kitchen")).check(matches(isDisplayed()));
        onView(withId(R.id.tag_edittext)).perform(ViewActions.typeText("KitcHen"));
        onView(withId(R.id.tag_create_button)).perform(click());
        onView(withText("Kitchen")).check(doesNotExist());
    }

    @Test
    public void testNoClosureIfNoTags(){
        // navigate to the fragment
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.edit_tag_button)).perform(click());

        // Try to apply no tags:
        onView(withId(R.id.tag_apply_button)).perform(click());
        // Check if TAGS frag still open
        onView(withText("TAGS")).check(matches(isDisplayed()));
    }

    @Test
    public void testTagFragBackButton(){
        // navigate to the fragment
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.edit_tag_button)).perform(click());

        // click back button
        onView(withId(R.id.tag_back_button)).perform(click());
        onView(withText("TAGS")).check(doesNotExist());
    }

    @Test
    public void testTagApplication(){
        // navigate to the fragment
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.edit_tag_button)).perform(click());

        onView(withId(R.id.tag_edittext)).perform(ViewActions.typeText("Kit"));
        onView(withId(R.id.tag_create_button)).perform(click());

        onData(anything())
                .inAdapterView(withId(R.id.tag_listview))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.tag_apply_button));
        onView(withText("Kit")).check(matches(isDisplayed()));

    }
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
}
