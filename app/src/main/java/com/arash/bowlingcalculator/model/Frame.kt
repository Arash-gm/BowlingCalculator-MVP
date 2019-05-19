package com.arash.bowlingcalculator.model

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

data class Frame(var firstAttempt: Int? = null, var secondAttempt: Int? = null, var result: Int = 0, var isStrike: Boolean = false, var isSpare: Boolean = false)