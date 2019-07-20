package com.diamondedge.ktsample

import android.app.Application
import android.content.Context

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: App

        fun get(): App = instance

        val context: Context
            get() = instance.applicationContext
    }
}