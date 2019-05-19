package com.arash.bowlingcalculator

import com.arash.bowlingcalculator.bowling.BowlingContract
import com.arash.bowlingcalculator.bowling.BowlingPresenter
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.INITIAL_FRAME_INDEX
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy

/**
 * Created by Arash Golmohammadi on 5/18/2019.
 */

class BowlingUnitTest {

    @Spy
    @InjectMocks
    lateinit var bowlingPresenter: BowlingPresenter

    @Mock
    lateinit var view: BowlingContract.View

    lateinit var frameStatus: FrameStatus


    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        //bowlingPresenter = BowlingPresenter(view)
        frameStatus = FrameStatus()
        bowlingPresenter.start(frameStatus,INITIAL_FRAME_INDEX)
    }

    @Test
    fun resetScreen() {
        bowlingPresenter.resetCalc()
        verify(view).resetViews()
    }

    @Test
    fun startPresenter() {
        assertEquals(0,INITIAL_FRAME_INDEX)
        verify(view).setActiveFrame(INITIAL_FRAME_INDEX)
    }

    @Test
    fun checkInput() {
        var shotInput = ""
        bowlingPresenter.checkInput(shotInput)
        verify(view).showSnackBarMsg(any())
        var shotInputInt = "4"
        bowlingPresenter.checkInput(shotInputInt)
        if(shotInputInt.toInt() in 0..10){
            verify(bowlingPresenter).analyzeShot(shotInputInt.toInt())
        }else{
            verify(view,times(2)).showSnackBarMsg(any())
        }
    }
}
