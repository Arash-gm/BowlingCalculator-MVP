package com.arash.bowlingcalculator.di.component

import com.arash.bowlingcalculator.base.ApplicationClass
import com.arash.bowlingcalculator.bowling.BowlingPresenter
import com.arash.bowlingcalculator.di.module.ActivityBuilderModule
import com.arash.bowlingcalculator.di.module.ApplicationModule
import com.arash.bowlingcalculator.model.FrameStatus
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * Created by Arash Golmohammadi on 5/16/2019.
 */

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, ActivityBuilderModule::class])
interface ApplicationComponent: AndroidInjector<ApplicationClass> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<ApplicationClass>()

    fun inject(bowlingPresenter: BowlingPresenter)
}
