package com.arash.bowlingcalculator.bowling

import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.MAX_SCORE
import com.arash.bowlingcalculator.util.util.NUMBER_OF_FRAMES
import com.arash.bowlingcalculator.util.util.SPARE_BONUS_POINT
import com.arash.bowlingcalculator.util.util.STRIKE_BONUS_POINT
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT
import com.arash.bowlingcalculator.util.util.isShotSpare
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
        if(frameStatus.total >= MAX_SCORE || isGameOver) return
        shotObservable.onNext(shotInput)
        when (shotInput) {
            STRIKE_SHOT -> performStrikeShot()
            in 0 until STRIKE_SHOT -> performNormalShot(shotInput)
        }
    }

    fun performNormalShot(shotInput: Int) {
        frameStatus.currentFrame.apply {
            if(isShotRangeValid(shotInput,result)){
                view?.showSnackBarMsg(R.string.invalid_shot)
                return
            }
            if(isShotSpare(shotInput,result)){
                performSpareShot(shotInput)
                return
            }
            when {
                firstAttempt == null -> {
                    firstAttempt = shotInput
                    result += shotInput
                    view?.setFrameFirstAttempt(shotInput)
                }
                secondAttempt == null -> {
                    secondAttempt = shotInput
                    result += shotInput
                    frameStatus.total += result
                    view?.setFrameSecondAttempt(shotInput)
                    view?.setFrameResult(frameStatus.total)
                    prepareNextFrame(this)
                }
                else -> {
                    if(firstAttempt!! + secondAttempt!! != STRIKE_SHOT){
                        gameOver()
                    }
                    thirdAttempt = shotInput
                    handleLastFrameData(isSpare = false,isStrike = false,shotInput = shotInput)
                }
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
                frameStatus.total += frameToRender.result
                view?.setStrikeFrameInRowResult(frameStatus.frameList.indexOf(frameToRender), frameStatus.total)
                disposableArray.remove().dispose()
            }
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
        var frameToRender: Frame? = null
        var queueHead = getBonusPointHead()
        when(queueHead.second){
            0 -> {
                frameToRender = queueHead.first
                removeBonusPointHead()
                fetchAndUpdateBonusListHead(shotInput)
            }
            else -> {
                queueHead.first.result += shotInput
                var newEntry = Pair(queueHead.first,queueHead.second - 1)

                if(queueHead.second - 1 == 0 && frameStatus.bonusPointQueue.size == 1){
                    frameToRender = queueHead.first
                    removeBonusPointHead()
                    if(frameStatus.bonusPointQueue.size > 0){
                        fetchAndUpdateBonusListHead(shotInput)
                    }
                }else{
                    updateBonusPointHead(newEntry)
                }
            }
        }

        return frameToRender
    }

    private fun fetchAndUpdateBonusListHead(shotInput: Int){
        var queueHead = getBonusPointHead()
        queueHead.first.result += shotInput
        var newEntry = Pair(queueHead.first,queueHead.second - 1)
        updateBonusPointHead(newEntry)
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
            when{
                isStrike -> {
                    var index = lastFrameViewRenderArray.indexOfFirst { !it }
                    if(index == -1) return
                    view?.renderLastFrame(index,isStrike,isSpare,shotInput)
                    lastFrameViewRenderArray[index] = true
                }
                isSpare -> {
                    view?.renderLastFrame(1,isStrike,isSpare,shotInput)
                    lastFrameViewRenderArray[1] = true
                }
                else -> {
                    view?.renderLastFrame(2,isStrike,isSpare,shotInput)
                    lastFrameViewRenderArray[2] = true
                }
            }

        }
    }

    fun isShotRangeValid(shotInput: Int,result: Int): Boolean{
        return shotInput + result > STRIKE_SHOT && activeFrameIndex != NUMBER_OF_FRAMES - 1
    }

    override fun resetCalc() {
        view?.resetViews()
    }

    private fun gameOver(){
        isGameOver = true
    }
}