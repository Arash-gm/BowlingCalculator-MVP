package com.arash.bowlingcalculator.bowling

import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.design.card.MaterialCardView
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.arash.bowlingcalculator.base.BaseActivity
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util.STRIKE_SHOT

import kotlinx.android.synthetic.main.activity_bowling.*
import kotlinx.android.synthetic.main.content_activity_bowling.*
import kotlinx.android.synthetic.main.view_card_frame_result.*
import javax.inject.Inject
import com.arash.bowlingcalculator.util.util.ANIMATION_DURATION
import com.arash.bowlingcalculator.util.util.INITIAL_ALPHA
import android.support.annotation.ColorInt
import android.util.TypedValue
import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.util.util.INITIAL_FRAME_INDEX
import com.arash.bowlingcalculator.util.util.isShotStrike

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

class BowlingActivity : BaseActivity(),BowlingContract.View, View.OnClickListener {

    @Inject
    override lateinit var presenter: BowlingContract.Presenter

    @Inject
    lateinit var frameStatus: FrameStatus

    private var activeFrame: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        initViews()
    }

    private fun initViews(){
        fab.setOnClickListener(this)
        hsvSmoothScroll(frameCard.getChildAt(0))
    }

    override fun initPresenter() {
        presenter.start(frameStatus,INITIAL_FRAME_INDEX)
    }

    override fun dropView() {
        presenter.dropView()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_bowling
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                presenter.resetCalc()
                true
            }
            else -> false
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when(v){
                fab -> {
                    presenter.checkInput(edtInputScore.text.toString())
                }
            }
        }
    }

    override fun setActiveFrame(index: Int) {
        activeFrame = frameCard.getChildAt(index)
        ObjectAnimator.ofFloat(frameCard.getChildAt(index),"alpha", INITIAL_ALPHA,1.0f).apply {
            duration = ANIMATION_DURATION
            start()
        }

        val typedValue = TypedValue()
        val theme = this.theme
        theme.resolveAttribute(R.attr.frameSelected, typedValue, true)
        @ColorInt val color = typedValue.data
        frameCard.getChildAt(index).findViewById<MaterialCardView>(R.id.baseFrameCard).strokeColor = color
    }

    override fun showSnackBarMsg(msg: Int) {
        Snackbar.make(fab, msg, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    override fun renderStrikeFrame() {
        activeFrame?.let {
            it.findViewById<TextView>(R.id.tvFirstAttempt).text = STRIKE_SHOT.toString()
            it.findViewById<TextView>(R.id.tvResult).text = ""
            var tvSecondAttempt = it.findViewById<TextView>(R.id.tvSecondAttempt)

            tvSecondAttempt.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
            tvSecondAttempt.text = ""
        }
    }

    override fun setStrikeFrameInRowResult(frameIndex: Int, score: Int) {
        var frameView = frameCard.getChildAt(frameIndex)
        frameView?.let {
            it.findViewById<TextView>(R.id.tvResult).text = score.toString()
            hsvSmoothScroll(frameView)
        }
    }

    private fun hsvSmoothScroll(frameView: View) {
        val endPos = frameView.x
        val halfWidth = frameView.width / 2

        hsv.smoothScrollTo((endPos + halfWidth).toInt(), 0)
    }

    override fun setLastFrameActive() {
        //activeFrame = frameResult.findViewById<TextView>(R.id.frameResultCardView)
    }

    override fun setResultFirstAttempt(isStrike: Boolean, isSpare: Boolean, score: Int) {
/*        var tvFirstAttempt = frameResult.findViewById<TextView>(R.id.tvResultFirstAttempt)
        when {
            isStrike -> tvFirstAttempt.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
            isSpare -> {

            }
            else -> {
                tvFirstAttempt.background = ContextCompat.getDrawable(this, R.color.white)
                tvFirstAttempt.text = score.toString()
            }
        }*/

    }

    override fun setResultSecondAttempt(isStrike: Boolean, isSpare: Boolean, score: Int) {
        /*var tvSecondAttempt = frameResult.findViewById<TextView>(R.id.tvResultSecondAttempt)
        when {
            isStrike -> tvSecondAttempt.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
            isSpare -> {
                tvSecondAttempt.background = ContextCompat.getDrawable(this, R.drawable.shape_triangle)
                tvSecondAttempt.text = ""
            }
            else -> {
                tvSecondAttempt.background = ContextCompat.getDrawable(this, R.color.white)
                tvSecondAttempt.text = score.toString()
            }
        }*/
    }

    override fun setResultThirdAttempt(isStrike: Boolean, isSpare: Boolean, score: Int) {
        /*var tvSecondAttempt = frameResult.findViewById<TextView>(R.id.tvResultThirdAttempt)
        when {
            isStrike -> tvSecondAttempt.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
            isSpare -> {

            }
            else -> {
                tvSecondAttempt.background = ContextCompat.getDrawable(this, R.color.white)
                tvSecondAttempt.text = score.toString()
            }
        }*/
    }

    override fun setResultLastFrame(total: Int) {
        //tvLastFrameResult.text = total.toString()
    }

    override fun resetViews() {
        this.recreate()
    }

    override fun renderNormalFrame(currentFrame: Frame) {
        currentFrame.firstAttempt?.let {
        setFrameFirstAttempt(it)
        }
        currentFrame.secondAttempt?.let {
            setFrameSecondAttempt(it)
        }
    }

    override fun setFrameFirstAttempt(firstAttempt: Int) {
        activeFrame?.let {
            it.findViewById<TextView>(R.id.tvFirstAttempt).text = firstAttempt.toString()
        }
    }

    override fun setFrameSecondAttempt(secondAttempt: Int) {
        activeFrame?.let {
            it.findViewById<TextView>(R.id.tvSecondAttempt).text = secondAttempt.toString()
        }
    }

    override fun setFrameResult(result: Int) {
        activeFrame?.let {
            it.findViewById<TextView>(R.id.tvResult).text = result.toString()
        }
    }

    override fun renderSpareFrame(currentFrame: Frame) {
        activeFrame?.let {
            var v = it.findViewById<TextView>(R.id.tvSecondAttempt)
            v.text = ""
            v.background = ContextCompat.getDrawable(this,R.drawable.shape_triangle)
        }
    }

    override fun renderLastFrame(viewIndex: Int, strike: Boolean, spare: Boolean, shotInput: Int?) {
        activeFrame?.let {
            when(viewIndex){
                0 -> {
                    var firstView = it.findViewById<TextView>(R.id.tvFirstAttempt)
                    if(strike){
                        firstView.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                    }else{
                        firstView.text = shotInput.toString()
                    }
                }
                1 -> {
                    var secondView = it.findViewById<TextView>(R.id.tvSecondAttempt)
                    if(strike){
                        secondView.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                    }else if(spare){
                        secondView.background = ContextCompat.getDrawable(this, R.drawable.shape_triangle)
                    }else{
                        secondView.text = shotInput.toString()
                    }
                }
                2 -> {
                    var thirdView = it.findViewById<TextView>(R.id.tvThirdAttempt)
                    if(strike){
                        thirdView.background = ContextCompat.getDrawable(this, R.color.colorPrimary)
                    }else{
                        thirdView.text = shotInput.toString()
                    }
                }
            }


        }
    }
}
