package com.arash.bowlingcalculator.di.module

import android.app.Application
import com.arash.bowlingcalculator.base.ApplicationClass
import com.arash.bowlingcalculator.model.Frame
import com.arash.bowlingcalculator.model.FrameStatus
import com.arash.bowlingcalculator.util.util
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Created by Arash Golmohammadi on 5/16/2019.
 */

@Module(includes = [AndroidInjectionModule::class])
abstract class ApplicationModule{


    @Binds
    internal abstract fun application(applicationClass: ApplicationClass): Application
}