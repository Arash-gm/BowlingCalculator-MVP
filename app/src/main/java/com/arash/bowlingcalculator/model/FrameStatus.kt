package com.arash.bowlingcalculator.model

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

data class FrameStatus ( val frameList: ArrayList<Frame> = arrayListOf()
                       , val bonusPointQueue : LinkedList<Pair<Frame,Int>> = LinkedList()
                       , var previousFrame: Frame? = Frame()
                       , var currentFrame: Frame = Frame()
                       , val waitingList: Queue<Frame> = LinkedList()
                       , var total: Int = 0)