package com.hyogeun.githubrepos.ui.etc

import android.app.Activity
import android.os.Bundle
import com.hyogeun.githubrepos.ui.main.MainActivity

class IntentFilterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.createInstance(this, intent?.data?.path)
        finish()
    }
}