package com.arash.bowlingcalculator.base

import com.arash.bowlingcalculator.model.FrameStatus

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

interface BasePresenter<T> {

    fun start(frameStatus: FrameStatus,initialFrameIndex: Int)
    fun dropView()
}