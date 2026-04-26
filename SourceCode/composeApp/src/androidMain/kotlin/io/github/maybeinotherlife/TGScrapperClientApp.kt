package io.github.maybeinotherlife

import android.app.Application
import android.content.Context

class TGScrapperClientApp: Application() {
    companion object{
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
    }
}