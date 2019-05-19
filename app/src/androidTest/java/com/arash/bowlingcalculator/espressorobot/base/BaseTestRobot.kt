package com.arash.bowlingcalculator.espressorobot.base

import android.support.annotation.StringRes
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import com.arash.bowlingcalculator.espressorobot.custom.CustomMatcher
import com.arash.bowlingcalculator.espressorobot.custom.CustomMatcher.checkChildCount

/**
 * Created by Arash Golmohammadi on 5/18/2019.
 */

open class BaseTestRobot{

    fun checkViewVisibility(resId: Int): ViewInteraction =
        onView(withId(resId)).check(matches(isDisplayed()))

    fun checkInputLength(resId: Int): ViewInteraction =
        onView(withId(resId)).check(matches(CustomMatcher.checkMaxLength(2)))

    fun checkInputIntegrity(resId: Int, shotInput: String): ViewInteraction =
        onView(withId(resId)).perform(ViewActions.replaceText(shotInput)).check(matches(withText(shotInput)))

    fun checkToolbarMenuItem(@StringRes menuItem: Int): ViewInteraction =
        onView(withText(menuItem)).check(matches(isDisplayed()))

    fun checkFrameCardChildCount(resId: Int,strikeShot: Int): ViewInteraction =
        onView(withId(resId)).check(matches(checkChildCount(strikeShot)))

    fun pressButton(resId: Int): ViewInteraction =
        onView(withId(resId)).perform(ViewActions.click())

    fun setInputField(resId: Int, shotInput: String): ViewInteraction =
        onView(withId(resId)).perform(ViewActions.replaceText(shotInput))

    fun checkSnackbarText(@StringRes errorMsg: Int): ViewInteraction =
        onView(withText(errorMsg)).check(matches(isDisplayed()))

}