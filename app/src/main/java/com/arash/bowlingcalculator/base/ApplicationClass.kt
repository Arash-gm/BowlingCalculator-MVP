package com.arash.bowlingcalculator.base

import android.app.Activity
import android.app.Application
import com.arash.bowlingcalculator.di.component.ApplicationComponent
import com.arash.bowlingcalculator.di.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

class ApplicationClass : Application(),HasActivityInjector{

    lateinit var applicationComponent: ApplicationComponent

    @Inject lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder().create(this) as ApplicationComponent
        applicationComponent.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    fun getComponent(): ApplicationComponent {
        return applicationComponent
    }
}