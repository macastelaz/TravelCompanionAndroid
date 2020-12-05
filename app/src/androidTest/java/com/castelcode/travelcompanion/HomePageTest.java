package com.castelcode.travelcompanion;

import android.content.Context;
import android.preference.Preference;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.GridView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withKey;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummary;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitle;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class HomePageTest {

    @Rule
    public ActivityTestRule<HomePage> mActivityRule = new ActivityTestRule<>(
            HomePage.class, false, false);

    @Test
    public void loaded_homePage() throws Exception {
        mActivityRule.launchActivity(null);
        onView(withId(R.id.tile_grid)).check(ViewAssertions.matches(withGridViewSize(7)));
    }

    @Test
    public void click_unitConverter() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(0).perform(click());
        onView(withId(R.id.unit_converter)).check(matches(isDisplayed()));
    }

    @Test
    public void click_tripLog() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(1).perform(click());
        onView(withId(R.id.trip_log)).check(matches(isDisplayed()));
    }

    @Test
    public void click_tripAgenda() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(2).perform(click());
        onView(withId(R.id.trip_agenda)).check(matches(isDisplayed()));
    }

    @Test
    public void click_drinkCounter() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(3).perform(click());
        onView(withId(R.id.drink_counter)).check(matches(isDisplayed()));
    }

    @Test
    public void click_tripInformation() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(4).perform(click());
        onView(withId(R.id.trip_information)).check(matches(isDisplayed()));
    }

    @Test
    public void click_expenses() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(5).perform(click());
        onView(withId(R.id.expenses)).check(matches(isDisplayed()));
    }

    @Test
    public void click_settings() throws Exception {
        mActivityRule.launchActivity(null);
        onData(anything()).inAdapterView(withId(R.id.tile_grid)).atPosition(6).perform(click());
        ViewInteraction dateOfTripLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        0),
                        isDisplayed()));
        dateOfTripLayout.check(matches(isDisplayed()));

        ViewInteraction timeOfTripLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        1),
                        isDisplayed()));
        timeOfTripLayout.check(matches(isDisplayed()));

        ViewInteraction notificationSettingsLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        2),
                        isDisplayed()));
        notificationSettingsLayout.check(matches(isDisplayed()));

        ViewInteraction saveCruiseLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        3),
                        isDisplayed()));
        saveCruiseLayout.check(matches(isDisplayed()));

        ViewInteraction loadCruiseLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        4),
                        isDisplayed()));
        loadCruiseLayout.check(matches(isDisplayed()));

        ViewInteraction deleteCruiseLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        5),
                        isDisplayed()));
        deleteCruiseLayout.check(matches(isDisplayed()));

        ViewInteraction resetCruiseLayout = onView(
                allOf(childAtPosition(
                        allOf(withId(android.R.id.list),
                                childAtPosition(
                                        withId(android.R.id.list_container),
                                        0)),
                        6),
                        isDisplayed()));
        resetCruiseLayout.check(matches(isDisplayed()));

        ViewInteraction dateTripTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Date of Trip"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        dateTripTitleTextView.check(matches(withText("Date of Trip")));

        ViewInteraction dateTripSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Please select the day that your trip begins"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        dateTripSummaryTextView.check(matches(withText("Please select the day that your trip begins")));

        ViewInteraction timeTripTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Time of Trip"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        timeTripTitleTextView.check(matches(withText("Time of Trip")));

        ViewInteraction timeTripSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Please select the time that your trip begins"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        timeTripSummaryTextView.check(matches(withText("Please select the time that your trip begins")));

        ViewInteraction notificationSettingsTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Notification Settings"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        notificationSettingsTitleTextView.check(matches(withText("Notification Settings")));

        ViewInteraction notificationSettingsSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Select how you wish to be notified about upcoming reservations."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        notificationSettingsSummaryTextView.check(matches(withText("Select how you wish to be notified about upcoming reservations.")));

        ViewInteraction saveCruiseTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Save Cruise"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        saveCruiseTitleTextView.check(matches(withText("Save Cruise")));

        ViewInteraction saveCruiseSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Save the current cruise if you want to come back to it after another cruise."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        saveCruiseSummaryTextView.check(matches(withText("Save the current cruise if you want to come back to it after another cruise.")));

        ViewInteraction loadCruiseTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Load a Cruise"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        loadCruiseTitleTextView.check(matches(withText("Load a Cruise")));

        ViewInteraction loadCruiseSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Load a cruise from earlier."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        loadCruiseSummaryTextView.check(matches(withText("Load a cruise from earlier.")));

        ViewInteraction deleteCruiseTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Delete a Cruise"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        deleteCruiseTitleTextView.check(matches(withText("Delete a Cruise")));

        ViewInteraction deleteCruiseSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Delete a cruise from earlier."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        deleteCruiseSummaryTextView.check(matches(withText("Delete a cruise from earlier.")));

        ViewInteraction resetCruiseTitleTextView = onView(
                allOf(withId(android.R.id.title), withText("Reset Cruise"),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                0),
                        isDisplayed()));
        resetCruiseTitleTextView.check(matches(withText("Reset Cruise")));

        ViewInteraction resetCruiseSummaryTextView = onView(
                allOf(withId(android.R.id.summary), withText("Reset the current cruise if you want to start from scratch."),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        0),
                                1),
                        isDisplayed()));
        resetCruiseSummaryTextView.check(matches(withText("Reset the current cruise if you want to start from scratch.")));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private static Matcher<View> withGridViewSize (final int size) {
        return new TypeSafeMatcher<View> () {
            @Override public boolean matchesSafely (final View view) {
                return ((GridView) view).getCount() == size;
            }

            @Override public void describeTo (final Description description) {
                description.appendText ("ListView should have " + size + " items");
            }
        };
    }
}
