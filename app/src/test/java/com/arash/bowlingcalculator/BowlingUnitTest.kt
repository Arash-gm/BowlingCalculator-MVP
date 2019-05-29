package com.arash.bowlingcalculator

import com.arash.bowlingcalculator.bowling.BowlingContract
import com.arash.bowlingcalculator.bowling.BowlingPresenter
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.INITIAL_FRAME_INDEX
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import java.util.*

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
        frameStatus = FrameStatus(frameList = arrayListOf(), bonusPointQueue = LinkedList())
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
        if(shotInputInt.toInt() in 0..STRIKE_SHOT){
            verify(bowlingPresenter).performShot(shotInputInt.toInt())
        }else{
            verify(view,times(2)).showSnackBarMsg(any())
        }
    }

    @Test
    fun performShot() {
        var shotInput = STRIKE_SHOT
        bowlingPresenter.performShot(shotInput)
        verify(bowlingPresenter).performStrikeShot()
        shotInput = 5
        bowlingPresenter.performShot(shotInput)
        verify(bowlingPresenter).performNormalShot(shotInput)
    }

    @Test
    fun performStrikeShot() {
        bowlingPresenter.performStrikeShot()
        var frame = Frame(secondAttempt = STRIKE_SHOT, result = STRIKE_SHOT, isStrike = true, isSpare = false)
        verify(bowlingPresenter).addToBonusPointList(frame)
        verify(bowlingPresenter).prepareNextFrame(frame)
        verify(view).renderStrikeFrame()
    }

    @Test
    fun checkPreviousStrike() {
        frameStatus.bonusPointQueue.add(Frame())
        frameStatus.bonusPointQueue.add(Frame())
    }

    @Test
    fun addToStrikeList(){
        var size = frameStatus.bonusPointQueue.size
        bowlingPresenter.addToBonusPointList(Frame())
        assertThat(frameStatus.bonusPointQueue.size, equalTo(++size))
    }

    @Test
    fun prepareNextFrame(){
        var size = frameStatus.frameList.size
        var frame = Frame()
        bowlingPresenter.prepareNextFrame(frame)
        assertThat(frameStatus.frameList.size, equalTo(++size))
        assertThat(frameStatus.currentFrame, equalTo(frame))
        verify(bowlingPresenter).incrementFrameIndex()
        verify(bowlingPresenter).setActiveFrameView()
    }

    @Test
    fun incrementFrameIndex(){
        var activeFrameIndex = bowlingPresenter.getActiveFrameIndex()
        bowlingPresenter.incrementFrameIndex()
        assertThat(bowlingPresenter.getActiveFrameIndex(), equalTo(++activeFrameIndex))
    }

    @Test
    fun setActiveFrameView(){
        bowlingPresenter.setActiveFrameView()
        verify(view, times(2)).setActiveFrame(bowlingPresenter.getActiveFrameIndex())
    }
}
