package com.kgxl.base

import android.app.Application
import android.content.Context
import com.kgxl.base.alive.*
import com.kgxl.toalive.live.Leoric
import com.kgxl.toalive.live.LeoricConfigs

/**
 * Created by kgxl on 2022/11/10
 */
class App : Application() {
    companion object{
        lateinit var application: Context
    }
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
        Leoric.init(this, LeoricConfigs(
            LeoricConfigs.LeoricConfig(
                "$packageName:resident",
                Service1::class.java.getCanonicalName(),
                Receiver1::class.java.getCanonicalName(),
                Activity1::class.java.getCanonicalName()),
            LeoricConfigs.LeoricConfig(
                "android.media",
                Service2::class.java.getCanonicalName(),
                Receiver2::class.java.getCanonicalName(),
                Activity2::class.java.getCanonicalName())
        ))
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        application = this
    }
}