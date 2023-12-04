package com.example.dwelventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

public class PhotoFragmentTests {

    @After
    public void tearDown() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    @Test
    public void testFragmentOpen(){
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.photo_button)).perform(click());
        onView(withText("PHOTOS")).check(matches(isDisplayed()));
    }

    @Test
    public void testOnConfirmClose(){
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.photo_button)).perform(click());
        onView(withText("PHOTOS")).check(matches(isDisplayed()));
        onView(withId(R.id.confirmButton)).perform(click());
        onView(withText("PHOTOS")).check(doesNotExist());
    }

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new ActivityScenarioRule<MainActivity>(MainActivity.class);
}
