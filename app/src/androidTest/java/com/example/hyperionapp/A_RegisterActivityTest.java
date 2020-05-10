package com.example.hyperionapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.EditText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.internal.runner.InstrumentationConnection;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static java.util.EnumSet.allOf;
import static org.junit.Assert.*;

public class A_RegisterActivityTest {

    @Rule
    public ActivityScenarioRule<RegisterActivity> registerActivityActivityScenarioRule = new ActivityScenarioRule<>(RegisterActivity.class);

    Instrumentation.ActivityMonitor createCodeMonitor = getInstrumentation().addMonitor(CreateCodeActivity.class.getName(),null, false);
    Instrumentation.ActivityMonitor loginMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(),null, false);

    String validEmail;
    String validPassword;
    String validUserCode;
    String registeredEmail;

    @Before
    public void setUp() throws Exception {
        String id = new Date().getTime() + "";
        validEmail = id + "@hyperion.com";
        registeredEmail = "x15037835@student.ncirl.ie";
        validPassword = "Password-1";
        validUserCode = "1234";
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testUserRegistration() {
        // Need to log out if currently logged in

        onView(withId(R.id.create_user_email))
                .perform(typeText(validEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.create_password_input))
                .perform(typeText(validPassword));
        onView(withId(R.id.confirm_password_input))
                .perform(typeText(validPassword));
        onView(withId(R.id.btn_create_user)).perform(click());

        Activity codeActivity = getInstrumentation().waitForMonitorWithTimeout(createCodeMonitor, 5000);
        assertNotNull(codeActivity);

        onView(withId(R.id.etCreateCode_text))
                .perform(typeText(validUserCode), closeSoftKeyboard());
        onView(withId(R.id.confirmCodeBtn)).perform(click());

        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(loginMonitor, 5000);
        assertNotNull(loginActivity);

        View login = loginActivity.findViewById(R.id.input_login_password);
        assertNotNull(login);
    }
}