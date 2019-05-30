package com.arash.bowlingcalculator.bowling

import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.MAX_SCORE
import com.arash.bowlingcalculator.util.util.NUMBER_OF_FRAMES
import com.arash.bowlingcalculator.util.util.SPARE_BONUS_POINT
import com.arash.bowlingcalculator.util.util.STRIKE_BONUS_POINT
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.*
import javax.inject.Inject


/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

class BowlingPresenter @Inject constructor(view: BowlingContract.View ): BowlingContract.Presenter{

    private var isGameOver: Boolean = false
    private var view: BowlingContract.View? = view
    private lateinit var frameStatus: FrameStatus
    private var activeFrameIndex: Int = 0
    private var lastFrameViewRenderArray: BooleanArray = booleanArrayOf(false,false,false)

    private val shotObservable = PublishSubject.create<Int>()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val disposableArray: Queue<Disposable> = LinkedList()

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
            shotInput.toInt() !in 0..STRIKE_SHOT -> view?.showSnackBarMsg(R.string.invalid_shot)
            shotInput.toInt() in 0..STRIKE_SHOT -> { performShot(shotInput.toInt())}
        }
    }

    fun performShot(shotInput: Int){
        if(frameStatus.total >= MAX_SCORE || isGameOver){
            return
        }
        shotObservable.onNext(shotInput)
        when (shotInput) {
            STRIKE_SHOT -> performStrikeShot()
            in 0 until STRIKE_SHOT -> performNormalShot(shotInput)
        }
    }

    fun performNormalShot(shotInput: Int) {
        frameStatus.apply {
            if(shotInput + currentFrame.result > STRIKE_SHOT){
                view?.showSnackBarMsg(R.string.invalid_shot)
                return
            }
            if(shotInput + currentFrame.result == STRIKE_SHOT){
                performSpareShot(shotInput)
                return
            }
            if(currentFrame.firstAttempt == null) {
                currentFrame.firstAttempt = shotInput
                currentFrame.result += shotInput
                view?.setFrameFirstAttempt(shotInput)
            }
            else {
                currentFrame.secondAttempt = shotInput
                currentFrame.result += shotInput
                frameStatus.total += currentFrame.result
                view?.setFrameSecondAttempt(shotInput)
                view?.setFrameResult(total)
                prepareNextFrame(currentFrame)
            }
        }
    }

    fun performStrikeShot() {
        frameStatus.apply {
            currentFrame.apply {
                isStrike = true
                secondAttempt = STRIKE_SHOT
                result = STRIKE_SHOT
            }

            var framePoint : Pair<Frame,Int> = Pair(currentFrame, STRIKE_BONUS_POINT)
            handleBonusShot(framePoint)
            when(activeFrameIndex){
                NUMBER_OF_FRAMES - 1 -> handleLastFrameData(isStrike = true)
                else -> view?.renderStrikeFrame()
            }
            prepareNextFrame(currentFrame)
        }
    }

    private fun handleBonusShot(bonusPoint: Pair<Frame,Int>) {
        addToBonusPointList(bonusPoint)
        var subscription = shotObservable.doOnSubscribe { t ->
            run {
                disposableArray.add(t)
            }
        }.subscribe {
            var frameToRender = updateQueueOnShot(it)
            frameToRender?.let {
                if (activeFrameIndex == NUMBER_OF_FRAMES - 1 && frameToRender.result == 20) {
                    frameToRender.result += STRIKE_SHOT
                }
                frameStatus.total += frameToRender.result
                view?.setStrikeFrameInRowResult(frameStatus.frameList.indexOf(frameToRender), frameStatus.total)
                disposableArray.remove().dispose()
                //removeBonusPointHead()
            }
            /*var bonusPoint = getBonusPointHead()
            bonusPoint--
            updateBonusPointHead(bonusPoint)
            frameToRender.result += it

            if (bonusPoint == 0) {
                if (setResultIndex == NUMBER_OF_FRAMES - 1 && frameToRender.result == 20) {
                    frameToRender.result += STRIKE_SHOT
                }
                frameStatus.total += frameToRender.result
                view?.setStrikeFrameInRowResult(setResultIndex, frameStatus.total)
                disposableArray.remove().dispose()
                removeBonusPointHead()
                incrementSetResultIndex()
            }*/
        }
        compositeDisposable.add(subscription)
    }

    private fun performSpareShot(shotInput: Int) {
        frameStatus.apply {
            currentFrame.apply {
                isStrike = false
                isSpare = true
                secondAttempt = shotInput
                result = STRIKE_SHOT
            }

            var framePoint : Pair<Frame,Int> = Pair(currentFrame, SPARE_BONUS_POINT)
            handleBonusShot(framePoint)
            when(activeFrameIndex){
                NUMBER_OF_FRAMES - 1 -> handleLastFrameData(isSpare = true)
                else -> view?.renderSpareFrame(currentFrame)
            }
            prepareNextFrame(currentFrame)
        }
    }

    fun updateQueueOnShot(shotInput: Int): Frame?{
        /*var iterator = frameStatus.bonusPointQueue.iterator()
        var frameToRender: Frame? = null
        while (iterator.hasNext()){
            var entry = iterator.next()
            when(entry.second){
                0 -> {
                    frameToRender = entry.first
                    removeBonusPointHead()
                }
                else -> performQueueUpdate(entry)
            }
        }*/
        var frameToRender: Frame? = null
        //var newArray : ArrayList<Pair<Frame,Int>> = arrayListOf()
        var queueHead = getBonusPointHead()
        when(queueHead.second){
            0 -> {
                frameToRender = queueHead.first
                removeBonusPointHead()
                var queueHead = getBonusPointHead()
                queueHead.first.result += shotInput
                var newEntry = Pair(queueHead.first,queueHead.second - 1)
                updateBonusPointHead(newEntry)
            }
            else -> {
                /*queueHead.first.result += shotInput
                var newEntry = Pair(queueHead.first,queueHead.second - 1)
                updateBonusPointHead(newEntry)*/

                queueHead.first.result += shotInput
                var newEntry = Pair(queueHead.first,queueHead.second - 1)

                if(queueHead.second - 1 == 0 && frameStatus.bonusPointQueue.size == 1){
                    frameToRender = queueHead.first
                    removeBonusPointHead()
                    if(frameStatus.bonusPointQueue.size > 0){
                        var queueHead = getBonusPointHead()
                        queueHead.first.result += shotInput
                        var newEntry = Pair(queueHead.first,queueHead.second - 1)
                        updateBonusPointHead(newEntry)
                    }
                }else{
                    updateBonusPointHead(newEntry)
                }
            }
        }

        return frameToRender
    }

    private fun performQueueUpdate(entry: Pair<Frame, Int>) {
        var newBonusPoint = entry.second
        var newPair : Pair<Frame,Int> = Pair(entry.first,--newBonusPoint)
        updateBonusPointHead(newPair)
    }

    private fun removeBonusPointHead(){
        frameStatus.bonusPointQueue.remove()
    }

    private fun getBonusPointHead(): Pair<Frame,Int> {
        return frameStatus.bonusPointQueue.peek()
    }

    private fun updateBonusPointHead(bonusPoint: Pair<Frame,Int>) {
        frameStatus.bonusPointQueue.remove()
        return frameStatus.bonusPointQueue.push(bonusPoint)
    }

    fun addToBonusPointList(bonusPoint: Pair<Frame,Int>){
        frameStatus.apply {
            bonusPointQueue.add(bonusPoint)
        }
    }

    fun prepareNextFrame(frame: Frame){
        if(activeFrameIndex == NUMBER_OF_FRAMES - 1){
            return
        }

        frameStatus.apply {
            when(frameList.size){
                0 -> frameList.add(frame)
                else -> frameList[activeFrameIndex] = frame
            }

            incrementFrameIndex()
            currentFrame = frameList[activeFrameIndex]
            setActiveFrameView()
        }
    }

    fun incrementFrameIndex(){
        activeFrameIndex++
    }

    fun setActiveFrameView(){
        view?.setActiveFrame(activeFrameIndex)
    }

    fun handleLastFrameData(isStrike: Boolean = false,isSpare: Boolean = false, shotInput: Int? = null){
        frameStatus.apply {
            var index = lastFrameViewRenderArray.indexOfFirst { !it }
            if(index == -1) return
            view?.renderLastFrame(index,isStrike,isSpare,shotInput)
            lastFrameViewRenderArray[index] = true
        }
    }

/*    fun incrementSetResultIndex(){
        if(setResultIndex == NUMBER_OF_FRAMES - 1)return
        setResultIndex++
    }*/

    fun getActiveFrameIndex(): Int{
        return activeFrameIndex
    }

    override fun resetCalc() {
        view?.resetViews()
    }

    private fun gameOver(){
        isGameOver = true
    }
}