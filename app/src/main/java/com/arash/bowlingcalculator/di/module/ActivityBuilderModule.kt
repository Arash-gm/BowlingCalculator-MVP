package com.arash.bowlingcalculator.di.module

import com.arash.bowlingcalculator.bowling.BowlingActivity
import dagger.android.ContributesAndroidInjector
import com.arash.bowlingcalculator.di.scope.PerActivity
import dagger.Module
import dagger.android.AndroidInjectionModule

/**
 * Created by Arash Golmohammadi on 5/16/2019.
 */

@Module(includes = [AndroidInjectionModule::class])
abstract class ActivityBuilderModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [BowlingActivityModule::class])
    internal abstract fun mainActivityInjector(): BowlingActivity

}