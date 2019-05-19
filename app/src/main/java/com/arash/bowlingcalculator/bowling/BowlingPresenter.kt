package com.arash.bowlingcalculator.bowling

import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.NUMBER_OF_FRAMES
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT
import com.arash.bowlingcalculator.util.util.isShotSpare
import com.arash.bowlingcalculator.util.util.isShotStrike
import javax.inject.Inject

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

class BowlingPresenter @Inject constructor(view: BowlingContract.View ): BowlingContract.Presenter{

    private var view: BowlingContract.View? = view
    private lateinit var frameStatus: FrameStatus
    private var activeFrameIndex: Int = 0

    override fun start(frameStatus: FrameStatus,initialFrameIndex: Int) {
        this.frameStatus = frameStatus
        view?.setActiveFrame(initialFrameIndex)
    }

    override fun dropView() {
        view = null
    }

    override fun checkInput(shotInput: String) {
        when{
            shotInput.isEmpty() -> view?.showSnackBarMsg(R.string.invalid_shot)
            shotInput.toInt() !in 0..10 -> view?.showSnackBarMsg(R.string.invalid_shot)
            else -> { analyzeShot(shotInput.toInt())}
        }
    }

    fun analyzeShot(shotInput: Int){
        frameStatus.currentFrame.apply {
            if(firstAttempt == null){
                if(isShotStrike(shotInput)){
                    performStrikeShot()
                }else{
                    performFirstAttemptShot(shotInput)
                }
            }else if(secondAttempt == null){
                if(checkSpareRange(firstAttempt!!,shotInput)){
                    if(isShotSpare(firstAttempt!!,shotInput)){
                        performSpareShot(shotInput)
                    }else{
                        performSecondAttemptShot(shotInput)
                    }
                }else{
                    view?.showSnackBarMsg(R.string.invalid_shot)
                }
            }
        }
    }

    private fun checkSpareRange(firstAttempt: Int, shotInput: Int): Boolean {
        return when{
            STRIKE_SHOT - (firstAttempt + shotInput) < 0 -> false
            else -> true
        }
    }

    private fun performSecondAttemptShot(shotInput: Int){
        frameStatus.apply {
            currentFrame.secondAttempt = shotInput
            currentFrame.result += shotInput
            checkPreviousStrike(this)
            frameStatus.total += currentFrame.result
            view?.setFrameResult(frameStatus.total)
            view?.renderNormalFrame(currentFrame)
            prepareNextFrame()
        }
    }

    private fun performSpareShot(shotInput: Int){
        frameStatus.apply {
            currentFrame.secondAttempt = shotInput
            currentFrame.isSpare = true
            currentFrame.result += shotInput
            checkPreviousStrike(this)
            strikeList.add(currentFrame)
            view?.renderSpareFrame(currentFrame)
            prepareNextFrame()
        }
    }

    private fun performFirstAttemptShot(shotInput: Int){
        frameStatus.apply {

            if(activeFrameIndex == NUMBER_OF_FRAMES - 1 && strikeList.isEmpty()){
                return
            }

            currentFrame.firstAttempt = shotInput
            currentFrame.result += shotInput
            if(strikeList.size == 2){
                checkPreviousStrike(this)
            }else{//checking Spare
                previousFrame?.let {
                    if(it.isSpare){
                        checkPreviousStrike(this)
                    }
                }
            }

            if(activeFrameIndex < NUMBER_OF_FRAMES - 3){
                view?.renderNormalFrame(currentFrame)
            }else{
                if(activeFrameIndex == NUMBER_OF_FRAMES - 2 && strikeList.size == 1){
                    checkPreviousStrike(this)
                }

                previousFrame?.let {
                    it.firstAttempt?.let {firstAttempt ->
                        if(isShotSpare(firstAttempt,currentFrame.firstAttempt!!)){
                            it.isSpare = true
                            setLastFrameData(activeFrameIndex,this)
                            prepareNextFrame()
                            return
                        }
                    }
                }
                setLastFrameData(activeFrameIndex,this)
                prepareNextFrame()
            }
        }
    }

    private fun performStrikeShot(){
        frameStatus.currentFrame.apply {
            isStrike = true
            firstAttempt = STRIKE_SHOT
            result = STRIKE_SHOT
        }

        frameStatus.apply {

            if(strikeList.size in 0..1){
                previousFrame?.let {
                    if(it.isSpare){
                        checkPreviousStrike(frameStatus)
                    }
                }
                strikeList.add(currentFrame)
                currentFrame.result = STRIKE_SHOT
                view?.renderStrikeFrame(currentFrame)
            }else{
                strikeList.add(currentFrame)
                currentFrame.result = STRIKE_SHOT
                if(activeFrameIndex < NUMBER_OF_FRAMES - 3){
                    view?.renderStrikeFrame(currentFrame)
                    setPreviousStrikeView(frameStatus)
                }else{
                    setLastFrameData(activeFrameIndex,this)
                    setPreviousStrikeView(frameStatus)
                }
            }
            prepareNextFrame()
        }
    }

    private fun checkPreviousStrike(frameStatus:FrameStatus){
        frameStatus.apply {
            if(strikeList.size in 1..2){
                if(activeFrameIndex < NUMBER_OF_FRAMES - 1){
                    setPreviousStrikeView(frameStatus)
                }
            }
        }
    }

    private fun setPreviousStrikeView(frameStatus: FrameStatus) {
        frameStatus.apply {
            var frameToRender = strikeList.remove()
            var index = frameList.indexOf(frameToRender)
            if(strikeList.size == 2){
                total += frameToRender.result + currentFrame.result + (STRIKE_SHOT)
            }else if(strikeList.size == 1){
                total += frameToRender.result + currentFrame.result + (STRIKE_SHOT)
            }else{
                total += frameToRender.result + currentFrame.result
            }

            frameToRender.result = total
            view?.setStrikeFrameInRowResult(index,frameToRender.result)
        }
    }

    private fun prepareNextFrame(){
        frameStatus.apply {
            if(activeFrameIndex < NUMBER_OF_FRAMES - 1){
                frameList[activeFrameIndex] = currentFrame
                previousFrame = currentFrame
                currentFrame = frameList[++activeFrameIndex]
                if(activeFrameIndex < NUMBER_OF_FRAMES - 3){
                    view?.setActiveFrame(activeFrameIndex)
                }
            }
        }
    }

    private fun setLastFrameData(activeFrameIndex: Int, frameStatus: FrameStatus){
        frameStatus.apply {
            when(activeFrameIndex){
                NUMBER_OF_FRAMES - 3 -> {
                    if(currentFrame.isStrike){
                        view?.setResultFirstAttempt(true)
                    }else{
                        total += currentFrame.result
                        view?.setResultFirstAttempt(false,score = currentFrame.result)
                    }
                }
                NUMBER_OF_FRAMES - 2 -> {
                    if(currentFrame.isStrike){
                        view?.setResultSecondAttempt(true)
                    }else{
                        previousFrame?.let {
                            it.isSpare?.let {isSpare ->
                                if(isSpare){
                                    currentFrame.isSpare = true
                                    strikeList.add(currentFrame)
                                    total += currentFrame.result
                                    view?.setResultSecondAttempt(isSpare = true)
                                }else{
                                    total += if(this.frameList[NUMBER_OF_FRAMES - 4].isSpare){
                                        currentFrame.result
                                    }else{
                                        currentFrame.result + it.result
                                    }
                                    view?.setResultSecondAttempt(false,score = currentFrame.result)
                                    view?.setResultLastFrame(total)
                                }
                            }?:run{
                                total += currentFrame.result + frameList[NUMBER_OF_FRAMES - 3].result
                                view?.setResultSecondAttempt(false,score = currentFrame.result)
                                view?.setResultLastFrame(total)
                            }
                        }?: run{
                            total += currentFrame.result + frameList[NUMBER_OF_FRAMES - 3].result
                            view?.setResultSecondAttempt(false,score = currentFrame.result)
                            view?.setResultLastFrame(total)
                        }
                    }
                }
                NUMBER_OF_FRAMES - 1 -> {
                    if(currentFrame.isStrike){
                        total += currentFrame.result * 3
                        view?.setResultThirdAttempt(true)
                        view?.setResultLastFrame(total)
                    }else{
                        if(previousFrame != null){
                            if(previousFrame!!.isSpare){
                                total += currentFrame.result + (STRIKE_SHOT - previousFrame!!.result)
                            }
                        }else{
                            total += currentFrame.result
                        }
                        view?.setResultThirdAttempt(false,score = currentFrame.result)
                        view?.setResultLastFrame(total)
                    }
                }
            }
        }
    }

    override fun resetCalc() {
        view?.resetViews()
    }
}