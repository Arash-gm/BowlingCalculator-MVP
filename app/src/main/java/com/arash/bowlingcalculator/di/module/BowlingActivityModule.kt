package com.arash.bowlingcalculator.di.module

import com.arash.bowlingcalculator.bowling.BowlingActivity
import com.arash.bowlingcalculator.bowling.BowlingContract
import com.arash.bowlingcalculator.bowling.BowlingPresenter
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util
import com.arash.bowlingcalculator.util.util.NUMBER_OF_FRAMES
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Arash Golmohammadi on 5/16/2019.
 */

@Module
abstract class BowlingActivityModule {

    @Binds
    abstract fun bowlingActivityView(bowlingActivity: BowlingActivity): BowlingContract.View


    @Module
    companion object Test {

        @JvmStatic
        @Provides
        fun provideBowlingPresenter(view: BowlingContract.View): BowlingContract.Presenter {
            return BowlingPresenter(view)
        }

        @JvmStatic
        @Provides
        fun provideFrameStatus(): FrameStatus {
            var frameStatus = FrameStatus()

            for(i in 1..NUMBER_OF_FRAMES){
                var frame = Frame()
                frameStatus.frameList.add(frame)
            }
            frameStatus.currentFrame = frameStatus.frameList[0]
            frameStatus.previousFrame = null
            return frameStatus
        }
    }

}