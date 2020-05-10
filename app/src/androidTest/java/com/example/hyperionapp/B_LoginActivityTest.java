package com.example.hyperionapp;

import android.app.Activity;
import android.app.Instrumentation;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class B_LoginActivityTest {
    /* TestCase to test all user login related UI functionality */

    // Rule which will start the LoginActivity
    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);
    // Instrumentation
    Instrumentation.ActivityMonitor mainMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null, false);

    String validPassword;
    String invalidEmail;
    String invalidPassword;
    String registeredEmail;

    @Before
    public void setUp() {
        //
        registeredEmail = "x15037835@student.ncirl.ie";
        validPassword = "Password-1";
        invalidEmail = "email@email.com";
        invalidPassword = "a";
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
    public void testNothingEntered() {

        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor, 1000);
        assertNull(mainActivity);

    }

    @Test
    public void testWrongPassword() {

        onView(withId(R.id.input_login_email))
                .perform(replaceText(registeredEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(invalidPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor, 1000);
        assertNull(mainActivity);

    }

    @Test
    public void testWrongEmail() {

        onView(withId(R.id.input_login_email))
                .perform(replaceText(invalidEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(validPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor, 1000);
        assertNull(mainActivity);

    }

    @Test
    public void testUserLogin() {

        onView(withId(R.id.input_login_email)).perform(replaceText(registeredEmail));
        onView(withId(R.id.input_login_password))
                .perform(replaceText(validPassword));
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor, 5000);
        assertNotNull(mainActivity);

    }
}