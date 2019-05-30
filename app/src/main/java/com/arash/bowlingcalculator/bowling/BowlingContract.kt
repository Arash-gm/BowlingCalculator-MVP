package com.arash.bowlingcalculator.bowling

import android.support.annotation.StringRes
import com.arash.bowlingcalculator.base.BasePresenter
import com.arash.bowlingcalculator.base.BaseView
import com.arash.bowlingcalculator.model.Frame

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

interface BowlingContract {

    interface View: BaseView<Presenter> {
        fun setActiveFrame(index: Int)
        fun showSnackBarMsg(@StringRes msg: Int)
        fun renderStrikeFrame()
        fun setStrikeFrameInRowResult(frameIndex: Int,score: Int)
        fun resetViews()
        fun renderNormalFrame(currentFrame: Frame)
        fun setFrameFirstAttempt(firstAttempt: Int)
        fun setFrameSecondAttempt(secondAttempt: Int)
        fun setFrameResult(result: Int)
        fun renderSpareFrame(currentFrame: Frame)
        fun renderLastFrame(viewIndex: Int = 0, strike: Boolean = false, spare: Boolean = false, shotInput: Int? = null)
    }

    interface Presenter: BasePresenter<View> {
        fun checkInput(shotInput: String)
        fun resetCalc()
    }
}