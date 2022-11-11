package com.kgxl.base

import android.app.Application

/**
 * Created by zjy on 2022/11/10
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}