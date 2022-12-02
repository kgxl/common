package com.kgxl.base

import android.app.Application
import com.kgxl.base.utils.SPUtils

/**
 * Created by zjy on 2022/11/10
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}