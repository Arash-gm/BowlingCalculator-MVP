package com.arash.bowlingcalculator.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import dagger.android.AndroidInjection

/**
 * Created by Arash Golmohammadi on 5/15/2019.
 */

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        initPresenter()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        dropView()
    }

    protected abstract fun initPresenter()
    protected abstract fun dropView()
    protected abstract fun getLayoutResourceId():Int
}