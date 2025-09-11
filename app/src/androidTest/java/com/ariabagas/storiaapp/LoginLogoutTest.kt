package com.ariabagas.storiaapp

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ariabagas.storiaapp.ui.home.HomeActivity
import com.ariabagas.storiaapp.ui.welcome.LoginActivity
import com.ariabagas.storiaapp.utils.EspressoIdlingResource
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.intent.Intents.times

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginLogoutTest {

    @Before
    fun setup() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLoginAndLogoutFlow() {
        // Launch LoginActivity directly
        ActivityScenario.launch(LoginActivity::class.java)

        // Reset Intents don't need to capture MainActivity -> LoginActivity (MainActivity just launcher)
        Intents.release()
        Intents.init()

        // login Screen
        onView(withId(R.id.edtEmail)).perform(typeText("aria@storia.com"), closeSoftKeyboard())
        onView(withId(R.id.edtPassword)).perform(typeText("aria1234"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        // HomeActivity Screen is launched (exactly once)
        Intents.intended(hasComponent(HomeActivity::class.java.name), times(1))
        // Scroll RecyclerView to simulate user interaction
        onView(withId(R.id.recyclerView)).perform(swipeUp())
        // logout
        onView(withId(R.id.btnLogout)).perform(click())

        // back to Login Screen (exactly once)
        Intents.intended(hasComponent(LoginActivity::class.java.name), times(1))
    }


    // Optional helper for manual waiting (e.g. animations)
    private fun waitFor(millis: Long): androidx.test.espresso.ViewAction {
        return object : androidx.test.espresso.ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription() = "Wait for $millis milliseconds."
            override fun perform(uiController: androidx.test.espresso.UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}
