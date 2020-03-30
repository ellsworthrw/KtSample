/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.diamondedge.ktsample

//import androidx.test.rule.ActivityTestRule

//import androidx.arch.core.executor.testing.InstantTaskExecutorRule

//import androidx.test.rule.ActivityTestRule

//import androidx.test.runner.AndroidJUnit4

//import androidx.arch.core.executor.testing.CountingTaskExecutorRule

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.fail
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@RunWith(AndroidJUnit4::class)
class BottomNavigationAndroidTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    //    @get:Rule var activityTestRule = ActivityTestRule(MainActivity::class.java)
    @get:Rule
    var activityScenarioRule = activityScenarioRule<MainActivity>()

//    @Rule
//    var testRule = CountingTaskExecutorRule()

    @Test
    fun bottomNavView_clickOnAllItems() {
        // All screens open at their first destinations
        assertFirstScreen()

        openThirdScreen()

        assertThirdScreen()

        openSecondScreen()

        assertSecondScreen()

        openFirstScreen()

        assertFirstScreen()
    }

    @Test
    fun bottomNavView_backGoesToFirstItem() {
        // From the 2nd or 3rd screens, back takes you to the 1st.
        openThirdScreen()

        pressBack()

        assertFirstScreen()
    }

    @Test(expected = NoActivityResumedException::class)
    fun bottomNavView_backfromFirstItemExits() {
        // From the first screen, back finishes the activity
        assertFirstScreen()

        pressBack() // This should throw NoActivityResumedException

        fail() // If it doesn't throw
    }

    fun withRecyclerView(recyclerViewId: Int): RecyclerViewMatcher {
        return RecyclerViewMatcher(recyclerViewId)
    }

    private fun selectItemInRecyclerView(recyclerViewId: Int) {
        onView(withRecyclerView(recyclerViewId).atPosition(0)).perform(click());
//        onView(withId(recyclerViewId))
//            .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()))
    }

    @Test
    fun bottomNavView_backstackMaintained() {
        // The back stack of any screen is maintained when returning to it
        openFirstScreen()

        selectItemInRecyclerView(R.id.recyclerview)

        assertDeeperFirstScreen()

        openThirdScreen()

        // Return to 1st
        openFirstScreen()

        // Assert it maintained the back stack
        assertDeeperFirstScreen()
    }

/*
    @Test
    fun bottomNavView_registerBackRegister() {
        openThirdScreen()

        pressBack() // This is handled in a especial way in code.

        openThirdScreen()

        onView(withContentDescription(R.string.sign_up))
            .perform(click())

        // Assert it maintained the back stack
        assertDeeperThirdScreen()
    }
*/

    @Test
    fun bottomNavView_itemReselected_goesBackToStart() {
        openFirstScreen()

        assertFirstScreen()

        selectItemInRecyclerView(R.id.recyclerview)

        assertDeeperFirstScreen()

        // Reselect the current item
        openFirstScreen()

        // Verify that it popped the back stack until the start destination.
        assertFirstScreen()
    }

    private fun assertSecondScreen() {
        onView(allOf(withText(R.string.reddit_title), isDescendantOfA(withId(R.id.action_bar))))
            .check(matches(isDisplayed()))
    }

    private fun openSecondScreen() {
        onView(allOf(withContentDescription(R.string.reddit_nav), isDisplayed()))
            .perform(click())
    }

    private fun openFirstScreen() {
        onView(allOf(withContentDescription(R.string.flickr_nav), isDisplayed()))
            .perform(click())
    }

    private fun assertFirstScreen() {
        onView(withId(R.id.flickr_screen))
            .check(matches(isDisplayed()))
    }

    private fun assertDeeperFirstScreen() {
        onView(withText(R.string.flickr_photo_title))
            .check(matches(isDisplayed()))
    }

    private fun openThirdScreen() {
        onView(allOf(withContentDescription(R.string.about_nav), isDisplayed()))
            .perform(click())
    }

    private fun assertThirdScreen() {
        onView(withText(R.string.about_description))
            .check(matches(isDisplayed()))
    }


    @Test
    @Throws(InterruptedException::class, TimeoutException::class)
    fun showSomeResults() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext<Context>(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activity = InstrumentationRegistry.getInstrumentation().startActivitySync(intent)
//        testRule.drainTasks(10, TimeUnit.SECONDS)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        val recyclerView = activity.findViewById<RecyclerView>(R.id.recyclerview)
        waitForAdapterChange(recyclerView)
        assertThat(recyclerView.getAdapter(), notNullValue())
        waitForAdapterChange(recyclerView)
        assertThat<Boolean>(recyclerView.getAdapter()!!.getItemCount() > 0, `is`<Boolean>(true))
    }

    @Throws(InterruptedException::class)
    private fun waitForAdapterChange(recyclerView: RecyclerView) {
        val latch = CountDownLatch(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            recyclerView.adapter!!.registerAdapterDataObserver(
                object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        latch.countDown()
                    }

                    override fun onChanged() {
                        latch.countDown()
                    }
                })
        }
        if (recyclerView.adapter!!.itemCount > 0) {
            return //already loaded
        }
        assertThat(latch.await(10, TimeUnit.SECONDS), `is`<Boolean>(true))
    }

}

