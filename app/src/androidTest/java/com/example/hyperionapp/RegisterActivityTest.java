package com.example.hyperionapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.view.View;

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
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;

public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> registerActivityTestRule = new ActivityTestRule<RegisterActivity>(RegisterActivity.class);
    @Rule
    public ActivityScenarioRule<RegisterActivity> registerActivityActivityScenarioRule = new ActivityScenarioRule<RegisterActivity>(RegisterActivity.class);

    Instrumentation.ActivityMonitor createCodeMonitor = getInstrumentation().addMonitor(CreateCodeActivity.class.getName(),null, false);
    Instrumentation.ActivityMonitor loginMonitor = getInstrumentation().addMonitor(LoginActivity.class.getName(),null, false);
    Instrumentation.ActivityMonitor mainMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null, false);

    private RegisterActivity registerActivity = null;
    String validEmail;
    String validPassword;
    String invalidEmail;
    String invalidPassword;
    String nonMatchingPassword;
    String validUserCode;
    String registeredEmail;

    @Before
    public void setUp() throws Exception {
        registerActivity = registerActivityTestRule.getActivity();
        String id = new Date().getTime() + "";
        validEmail = id + "@hyperion.com";
        registeredEmail = "x15037835@student.ncirl.ie";
        validPassword = "Password-1";
        invalidEmail = "email";
        invalidPassword = "a";
        nonMatchingPassword  = "b";
        validUserCode = "1234";

    }

    @After
    public void tearDown() throws Exception {
        registerActivity = null;
    }

    @Test
    public void onCreate() {
        View view = registerActivity.findViewById(R.id.btn_create_user);
        assertNotNull(view);
    }

    @Test
    public void testUserRegistration() {
        // Need to log out if currently logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
            onView(withText("Logout")).perform(click());
            Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(loginMonitor, 5000);
            onView(withId(R.id.link_signup)).perform(click());
        }

        View view = registerActivity.findViewById(R.id.btn_create_user);
        assertNotNull(view);

        onView(withId(R.id.create_user_email))
                .perform(typeText(validEmail), closeSoftKeyboard());
        onView(withId(R.id.create_password_input))
                .perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.confirm_password_input))
                .perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.btn_create_user)).perform(click());

        Activity codeActivity = getInstrumentation().waitForMonitorWithTimeout(createCodeMonitor, 5000);
        assertNotNull(codeActivity);

        onView(withId(R.id.etCreateCode_text))
                .perform(typeText(validUserCode), closeSoftKeyboard());
        onView(withId(R.id.confirmCodeBtn)).perform(click());

        Activity loginActivity = getInstrumentation().waitForMonitorWithTimeout(loginMonitor, 5000);
        assertNotNull(loginActivity);

        View login = loginActivity.findViewById(R.id.btn_login);
        assertNotNull(login);
    }
}