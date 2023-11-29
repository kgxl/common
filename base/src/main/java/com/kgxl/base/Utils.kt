package com.kgxl.base

import android.annotation.SuppressLint
import android.app.Application
import com.hjq.toast.ToastUtils
import com.kgxl.base.utils.SPUtils

/**
 * Created by kgxl on 2022/11/10
 */
class Utils private constructor() {
    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var sApp: Application? = null

        /**
         * Init utils.
         *
         * Init it in the class of UtilsFileProvider.
         *
         * @param app application
         */
        fun init(app: Application) {
            SPUtils.init(app)
            ToastUtils.init(app)
            if (sApp == null) {
                sApp = app
                return
            }
            if (sApp == app) return
            sApp = app
        }

        /**
         * Return the Application object.
         *
         * Main process get app by UtilsFileProvider,
         * and other process get app by reflect.
         *
         * @return the Application object
         */
        val app: Application
            get() {
                if (sApp == null) throw NullPointerException("reflect failed.")
                return sApp as Application
            }

        fun isSpace(s: String): Boolean {
            var i = 0
            val len = s.length
            while (i < len) {
                if (!Character.isWhitespace(s[i])) {
                    return false
                }
                ++i
            }
            return true
        }
    }
}