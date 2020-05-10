package com.example.hyperionapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.guieffect.qual.SafeType;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.contrib.ViewPagerActions.scrollLeft;
import static androidx.test.espresso.contrib.ViewPagerActions.scrollRight;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;

public class C_MainActivityTest {
    String registeredEmail;
    String validPassword;

    String name;
    String address1;
    String address2;
    String city;
    String postCode;
    String dob;
    String pps;
    String allergies;
    String other_conditions;
    String medications;
    String height;
    String weight;
    String gp;
    String symptoms;
    String validUserCode;

    // Rule which will start the LoginActivity
    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);
    // Instrumentation
    Instrumentation.ActivityMonitor mainActivityMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null, false);
    Instrumentation.ActivityMonitor singleSessionMonitor = getInstrumentation().addMonitor(SingleSessionActivity.class.getName(),null, false);

    @Rule
    public ActivityTestRule mActivity = new ActivityTestRule<>(
            MainActivity.class,true, false);

    @Before
    public void setUp() {
        registeredEmail = "x15037835@student.ncirl.ie";
        validPassword = "Password-1";
        name = "Alex Doe";
        address1 = "Main Street";
        address2 = "Dublin Road";
        city = "Dublin";
        postCode = "D1 BW345";
        dob = "1987-11-16";
        pps = "123456AB";
        allergies = "nuts";
        other_conditions = "Asthma";
        medications = "Aspirin";
        height = "6.4";
        weight = "85";
        gp = "James Monroe";
        symptoms = "Headache, fever";
        validUserCode = "1234";

        try {
            FirebaseAuth.getInstance().signOut();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSaveDetails() {
        // Need to login first to be able to access the MainActivity
        onView(withId(R.id.input_login_email)).perform(replaceText(registeredEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(validPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5000);
        assertNotNull(mainActivity);

        onView(withId(R.id.view_pager)).perform(scrollLeft());
        onView(withId(R.id.view_pager)).perform(scrollLeft());

        onView(withId(R.id.name_edit_text)).perform(replaceText(name));
        onView(withId(R.id.address_edit_text)).perform(replaceText(address1));
        onView(withId(R.id.address2_edit_text)).perform(replaceText(address2));
        onView(withId(R.id.city_edit_text)).perform(replaceText(city));
        onView(withId(R.id.post_code_edit_text)).perform(replaceText(postCode));
        onView(withId(R.id.email_edit_text)).perform(replaceText(registeredEmail));
        // Reference: https://stackoverflow.com/a/43180527
        onView(withId(R.id.dob_edit_text)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(1987, 11, 16));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.pps_edit_text)).perform(replaceText(pps));

        onView(withId(R.id.save_button)).perform(click());

        onView(withId(R.id.view_pager)).perform(scrollRight());

        onView(withId(R.id.bloodtype_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.bloodtype_spinner)).check(ViewAssertions.matches(withSpinnerText(containsString("A"))));

        onView(withId(R.id.allergies_edit_text)).perform(replaceText(allergies));
        onView(withId(R.id.other_conditions_edit_text)).perform(replaceText(other_conditions));
        onView(withId(R.id.tubercolosis_checkbox)).perform(click());
        onView(withId(R.id.heart_condition_checkbox)).perform(click());
        onView(withId(R.id.epilepsy_checkbox)).perform(click());
        onView(withId(R.id.smoker_checkbox)).perform(click());
        onView(withId(R.id.diabetes_checkbox)).perform(click());
        onView(withId(R.id.gloucoma_checkbox)).perform(click());
        onView(withId(R.id.alcohol_drug_checkbox)).perform(click());
        onView(withId(R.id.cancer_checkbox)).perform(click());
        onView(withId(R.id.medication_edit_text)).perform(replaceText(medications));
        onView(withId(R.id.height_edit_text)).perform(replaceText(height));
        onView(withId(R.id.weight_edit_text)).perform(replaceText(weight));
        onView(withId(R.id.gp_edit_text)).perform(replaceText(gp));

        onView(withId(R.id.save_button2)).perform(click());

    }

    @Test
    public void testCheckin() {
        // Need to login first to be able to access the MainActivity
        onView(withId(R.id.input_login_email)).perform(replaceText(registeredEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(validPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5000);
        assertNotNull(mainActivity);

        onView(withId(R.id.symptoms_text)).perform(replaceText(symptoms));

        onView(withId(R.id.pain_scale_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.pain_scale_spinner)).check(ViewAssertions.matches(withSpinnerText(containsString("Very Painful"))));


        onView(withId(R.id.duration_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.duration_spinner)).check(ViewAssertions.matches(withSpinnerText(containsString("Less than 1 hour"))));

        onView(withId(R.id.subscribeButton)).perform(click());

    }

    @Test
    public void testlistDocuments() {
        // Need to login first to be able to access the MainActivity
        onView(withId(R.id.input_login_email)).perform(replaceText(registeredEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(validPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainActivityMonitor, 5000);
        assertNotNull(mainActivity);

        onView(withId(R.id.symptoms_text)).perform(replaceText(symptoms));

        onView(withId(R.id.pain_scale_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.pain_scale_spinner)).check(ViewAssertions.matches(withSpinnerText(containsString("Very Painful"))));


        onView(withId(R.id.duration_spinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.duration_spinner)).check(ViewAssertions.matches(withSpinnerText(containsString("Less than 1 hour"))));

        onView(withId(R.id.subscribeButton)).perform(click());

        mActivity.launchActivity(null);

        onView(withId(R.id.view_pager)).perform(scrollRight());
        onData(anything()).inAdapterView(withId(R.id.session_list)).atPosition(0).perform(click());

        Activity singleSession = getInstrumentation().waitForMonitorWithTimeout(singleSessionMonitor, 5000);
        assertNotNull(singleSession);

    }


}
