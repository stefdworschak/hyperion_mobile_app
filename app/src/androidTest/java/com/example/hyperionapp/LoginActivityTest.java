package com.example.hyperionapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.view.View;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
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
import static org.junit.Assert.assertNotNull;

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);
    @Rule
    public ActivityScenarioRule<LoginActivity> loginActivityActivityScenarioRule = new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

    Instrumentation.ActivityMonitor mainMonitor = getInstrumentation().addMonitor(MainActivity.class.getName(),null, false);

    private LoginActivity loginActivity = null;
    String validEmail;
    String validPassword;
    String invalidEmail;
    String invalidPassword;
    String nonMatchingPassword;
    String validUserCode;
    String registeredEmail;

    @Before
    public void setUp() throws Exception {
        loginActivity = loginActivityTestRule.getActivity();
        String id = new Date().getTime() + "";
        validEmail = id.substring(0,5) + "_test@hyperion.com";
        registeredEmail = "x15037835@student.ncirl.ie";
        validPassword = "Password-1";
        invalidEmail = "email";
        invalidPassword = "a";
        nonMatchingPassword  = "b";
        validUserCode = "1234";
       /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseAuth.getInstance().signOut();
        }*/
    }

    @After
    public void tearDown() throws Exception {
        loginActivity = null;
    }

    @Test
    public void onCreate() {
        View view = loginActivity.findViewById(R.id.btn_create_user);
        assertNotNull(view);
    }


    @Test
    public void testUserLogin() {

        // Need to log out if currently logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
            onView(withText("Logout")).perform(click());
        }

        onView(withId(R.id.input_login_email))
                .perform(typeText(registeredEmail), closeSoftKeyboard());
        onView(withId(R.id.input_login_password))
                .perform(typeText(validPassword), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        Activity mainActivity = getInstrumentation().waitForMonitorWithTimeout(mainMonitor, 5000);
        assertNotNull(mainActivity);

        View main = mainActivity.findViewById(R.id.subscribeButton);
        assertNotNull(main);

    }

}