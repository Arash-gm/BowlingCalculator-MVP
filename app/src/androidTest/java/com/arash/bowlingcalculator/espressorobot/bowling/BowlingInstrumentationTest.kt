package com.arash.bowlingcalculator.espressorobot.bowling

import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.arash.bowlingcalculator.bowling.BowlingActivity
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Arash Golmohammadi on 5/18/2019.
 */

@RunWith(AndroidJUnit4::class)
class BowlingInstrumentationTest{

    @get:Rule
    val activityTestRule: ActivityTestRule<BowlingActivity> = ActivityTestRule(BowlingActivity::class.java)

    @Test
    fun checkScreen(){
        Bowling {
            checkInputVisibility()
            checkInputLength()
            checkInputIntegrity("10")

            checkToolbarVisibility()
            checkHsvVisibility()

            checkFabVisibility()
            checkFrameCardVisibility()

            checkFrameCardChildCount()
        }
    }

    @Test
    fun checkToolbarMenu(){
        Bowling {
            openActionBarOverflowOrOptionsMenu(getInstrumentation().targetContext)
            checkToolbarMenuItem()
        }
    }

    @Test
    fun testErrorMsg(){
        Bowling {
            pressFabButton()
            setInputField((STRIKE_SHOT + 1).toString())
            checkSnackbarText()
        }
    }
}