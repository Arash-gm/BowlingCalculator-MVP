package com.arash.bowlingcalculator.util

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

object util{
    val INITIAL_FRAME_INDEX = 0
    val NUMBER_OF_FRAMES = 10
    val STRIKE_SHOT = 10
    val MAX_SCORE = 300
    val ANIMATION_DURATION = 400L
    val INITIAL_ALPHA = 0.4f

    fun isShotStrike(shot: Int): Boolean{
        return shot == 10
    }

    fun isShotSpare(firstAttempt: Int, secondAttempt :Int): Boolean{
        return (firstAttempt + secondAttempt) - STRIKE_SHOT == 0
    }
}