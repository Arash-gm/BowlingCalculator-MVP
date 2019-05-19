package com.arash.bowlingcalculator.espressorobot.custom

import android.text.InputFilter
import android.widget.TextView
import android.os.Build
import android.support.annotation.RequiresApi
import android.view.View
import com.arash.bowlingcalculator.util.util
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import android.view.ViewGroup
import org.hamcrest.Matcher


/**
 * Created by Arash Golmohammadi on 5/18/2019.
 */

object CustomMatcher{

    fun checkMaxLength(length: Int): TypeSafeMatcher<View> {
        return object : TypeSafeMatcher<View>(){

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun matchesSafely(item: View): Boolean {
                val filters = (item as TextView).filters
                val lengthFilter = filters[0] as InputFilter.LengthFilter

                return lengthFilter.max == length
            }

            override fun describeTo(description: Description) {
                description.appendText("check max length , View should contain numbers between zero and " + util.STRIKE_SHOT)
            }
        }
    }


    fun checkChildCount(childCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {

            public override fun matchesSafely(view: View): Boolean {
                return view is ViewGroup && childCount == view.childCount
            }

            override fun describeTo(description: Description) {
                description.appendText("children count doesn't match with provided view")
            }
        }
    }

}