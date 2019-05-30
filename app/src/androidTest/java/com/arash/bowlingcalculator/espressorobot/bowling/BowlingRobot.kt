package com.arash.bowlingcalculator.espressorobot.bowling

import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.espressorobot.base.BaseTestRobot
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT

/**
 * Created by Arash Golmohammadi on 5/18/2019.
 */

fun Bowling(func: BowlingRobot.() -> Unit) = BowlingRobot().apply{func()}

class BowlingRobot : BaseTestRobot(){

    var edtInput = R.id.edtInputScore

    fun checkInputVisibility() = checkViewVisibility(edtInput)
    fun checkInputLength() = checkInputLength(edtInput)
    fun checkInputIntegrity(shotInput: String) = checkInputIntegrity(edtInput,shotInput)

    fun checkToolbarVisibility() = checkViewVisibility(R.id.toolbar)
    fun checkToolbarMenuItem() = checkToolbarMenuItem(R.string.action_reset)

    fun checkFabVisibility() = checkViewVisibility(R.id.fab)
    fun checkHsvVisibility() = checkViewVisibility(R.id.hsv)

    fun checkFrameCardVisibility() = checkViewVisibility(R.id.frameCard)
    fun checkFrameCardChildCount() = checkFrameCardChildCount(R.id.frameCard, STRIKE_SHOT)

    fun pressFabButton() = pressButton(R.id.fab)
    fun setInputField(shotInput: String) = setInputField(edtInput,shotInput)
    fun checkSnackbarText() = checkSnackbarText(R.string.invalid_shot)
}