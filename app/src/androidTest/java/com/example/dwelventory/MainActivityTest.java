package com.example.dwelventory;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertNotNull;

import android.widget.ListView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
    public void testAddItem(){
        // add two items
        onView(withText("Total Cost"));
//        Item item1 = new Item("Billy", date1, "Pygmy Goat", "Caramel w/ Black Markings",serial,200, comment, photos);
//        Item item2 = new Item("Jinora", date2, "Pygmy Goat", "Caramel w/ Black Markings", 200);
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
    public void testEditItem(){
        ActivityScenario<MainActivity> scenario = mainScenario.getScenario();
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Ozzie"));
        onView(withId(R.id.date_button)).perform(ViewActions.typeText("09-11-2076"));
        onView(withId(R.id.make_button)).perform(ViewActions.typeText("Pygmy Goat"));
        onView(withId(R.id.model_button)).perform(ViewActions.typeText("Caramel w/ Black Markings"));
        Espresso.pressBack();
        onView(withId(R.id.estimated_val_button)).perform(ViewActions.typeText("200"));
        Espresso.pressBack();
        onView(withText("Confirm")).perform(click());


        scenario.onActivity(activity -> {
            ListView itemList = activity.findViewById(R.id.item_list);
            assertNotNull("The list was not loaded", itemList);
            itemList.performItemClick(itemList.getAdapter().getView(0,null,null),0,itemList.getAdapter().getItemId(0));
        });
        onView(withId(R.id.item_name_button)).perform(ViewActions.replaceText(""));
        onView(withId(R.id.item_name_button)).perform(ViewActions.clearText());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Oz"));
        onView(withText("Confirm")).perform(click());
        // accidentally types Ozg not Oz
        onView(withText("Ozg")).check(matches(isDisplayed()));
    }
    @Test
    public void testDeleteItem(){
        onView(withId(R.id.add_item_button)).perform(click());
        onView(withId(R.id.item_name_button)).perform(ViewActions.typeText("Alfie"));
        onView(withId(R.id.date_button)).perform(ViewActions.typeText("01-04-1999"));
        onView(withId(R.id.make_button)).perform(ViewActions.typeText("Pygmy Goat"));
        onView(withId(R.id.model_button)).perform(ViewActions.typeText("Caramel w/ Black Markings"));
        Espresso.pressBack();
        onView(withId(R.id.estimated_val_button)).perform(ViewActions.typeText("200"));
        Espresso.pressBack();
        onView(withText("Confirm")).perform(click());
        onView(withText("Alfie")).check(matches(isDisplayed()));
        onView(withText("Alfie")).perform(longClick());
        // delete
        onView(withText("Alfie")).check(doesNotExist());
    }


    @Test
    public void testTotalCost(){
        // delete all items
        // add new item of cost 5
        // check displays 5
        // edit item to cost 2
        // check displays 2
        // add new item of cost 10
        // check displays 12
    }
}
