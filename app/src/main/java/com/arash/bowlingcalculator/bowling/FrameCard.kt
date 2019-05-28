package com.arash.bowlingcalculator.bowling

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.view.LayoutInflater
import com.arash.bowlingcalculator.R
import com.arash.bowlingcalculator.util.util.NUMBER_OF_FRAMES

/**
 * Created by Arash Golmohammadi on 5/16/2019.
 */

class FrameCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0):LinearLayout(context, attrs, defStyleAttr, defStyleRes){

    var frameCount = 10

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_HORIZONTAL

        attrs?.let {
            val attr = context.obtainStyledAttributes(attrs, R.styleable.frame_cards, 0, 0)
            frameCount = attr.getInt(R.styleable.frame_cards_frameCount,frameCount)
        }
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        for(i in 1..frameCount){
            when{
                i < frameCount -> {
                    var childView = inflater.inflate(R.layout.view_card_frame, this, false)
                    addView(childView)
                }else -> {
                    var childView = inflater.inflate(R.layout.view_card_frame_result, this, false)
                    addView(childView)
                }
            }
        }
    }
}