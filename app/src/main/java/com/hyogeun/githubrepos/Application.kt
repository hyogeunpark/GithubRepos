package com.hyogeun.githubrepos

import android.app.Application
import android.content.Context

class Application: Application() {

    companion object {
        private lateinit var sContext: Context
        fun getContext() = sContext
    }

    override fun onCreate() {
        super.onCreate()
        sContext = this
    }
}