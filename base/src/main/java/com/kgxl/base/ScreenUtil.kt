package com.kgxl.base

import com.kgxl.base.ScreenUtil
import android.util.DisplayMetrics
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.view.WindowManager
import android.os.Build
import android.view.DisplayCutout
import android.text.TextUtils
import java.lang.ClassCastException
import java.lang.Exception

object ScreenUtil {
    private const val TAG = "ScreenUtil"
    var screenWidth = 0
    var screenHeight = 0
    var screenMin // 宽高中，小的一边
            = 0
    var screenMax // 宽高中，较大的值
            = 0
    var density = 0f
    var scaleDensity = 0f
    var xdpi = 0f
    var ydpi = 0f
    var densityDpi = 0
    var statusbarheight = 0
    var navbarheight = 0

    init {
        init()
    }

    fun dip2px(dipValue: Float): Int {
        return (dipValue * density + 0.5f).toInt()
    }

    fun px2dip(pxValue: Float): Int {
        return (pxValue / density + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (spValue * scaleDensity + 0.5f).toInt()
    }

    val displayWidth: Int
        get() {
            if (screenWidth == 0) {
                init()
            }
            return screenWidth
        }
    val displayHeight: Int
        get() {
            if (screenHeight == 0) {
                init()
            }
            return screenHeight
        }

    fun init() {
        val dm = Resources.getSystem().displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        screenMin = if (screenWidth > screenHeight) screenHeight else screenWidth
        screenMax = if (screenWidth < screenHeight) screenHeight else screenWidth
        density = dm.density
        scaleDensity = dm.scaledDensity
        xdpi = dm.xdpi
        ydpi = dm.ydpi
        densityDpi = dm.densityDpi
        statusbarheight = statusBarHeight
        navbarheight = navBarHeight
    }

    // 资源id
    // 反射调用
    // 默认高度
    val statusBarHeight: Int
        get() {
            // 资源id
            if (statusbarheight <= 0) {
                val resourceId =
                    Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
                if (resourceId > 0) {
                    statusbarheight = Resources.getSystem().getDimensionPixelSize(resourceId)
                }
            }
            // 反射调用
            if (statusbarheight <= 0) {
                try {
                    val c = Class.forName("com.android.internal.R\$dimen")
                    val o = c.newInstance()
                    val field = c.getField("status_bar_height")
                    val x = field[o] as Int
                    statusbarheight = Resources.getSystem().getDimensionPixelSize(x)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            // 默认高度
            if (statusbarheight <= 0) {
                statusbarheight = dip2px(25f)
            }
            return statusbarheight
        }
    val navBarHeight: Int
        get() {
            val resources = Resources.getSystem()
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else 0
        }

    /**
     * 判断是否显示了导航栏
     * (说明这里的context 一定要是activity的context 否则类型转换失败)
     *
     * @param context
     * @param isShowStatusBar 是否沉浸了
     * @return
     */
    fun isShowNavBar(context: Context?, isShowStatusBar: Boolean): Boolean {
        if (null == context) {
            return false
        }
        /**
         * 获取应用区域高度
         */
        val outRect1 = Rect()
        try {
            (context as Activity).window.decorView.getWindowVisibleDisplayFrame(outRect1)
        } catch (e: ClassCastException) {
            e.printStackTrace()
            return false
        }
        val activityHeight = outRect1.height()

        /**
         * 获取状态栏高度
         */
        var statuBarHeight = 0
        if (isShowStatusBar) {
            statuBarHeight = statusBarHeight
        }
        /**
         * 屏幕物理高度 减去 状态栏高度
         */
        val remainHeight = getRealHeight(context) - statuBarHeight
        /**
         * 剩余高度跟应用区域高度相等 说明导航栏没有显示 否则相反
         */
        return if (activityHeight == remainHeight) {
            false
        } else {
            true
        }
    }

    fun getActivityHeight(context: Context?): Int {
        return if (null == context) {
            screenHeight
        } else try {
            (context as Activity).window.decorView.height
        } catch (e: ClassCastException) {
            e.printStackTrace()
            screenHeight
        }
    }

    /**
     * 活动屏幕信息
     */
    private var wm: WindowManager? = null
    fun getRealHeight(context: Context): Int {
        if (null == wm) {
            wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm!!.defaultDisplay.getRealSize(point)
        } else {
            wm!!.defaultDisplay.getSize(point)
        }
        return point.y
    }

    /**
     * 是否有刘海屏
     *
     * @return
     */
    fun hasNotchInScreen(activity: Activity): Boolean {

        // android  P 以上有标准 API 来判断是否有刘海屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val displayCutout = activity.window.decorView.rootWindowInsets.displayCutout
            if (displayCutout != null) {
                // 说明有刘海屏
                return true
            }
        } else {
            // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
            val manufacturer = Build.MANUFACTURER
            return if (TextUtils.isEmpty(manufacturer)) {
                false
            } else if (manufacturer.equals("HUAWEI", ignoreCase = true)) {
                hasNotchHw(activity)
            } else if (manufacturer.equals("xiaomi", ignoreCase = true)) {
                hasNotchXiaoMi(activity)
            } else if (manufacturer.equals("oppo", ignoreCase = true)) {
                hasNotchOPPO(activity)
            } else if (manufacturer.equals("vivo", ignoreCase = true)) {
                hasNotchVIVO(activity)
            } else {
                false
            }
        }
        return false
    }

    /**
     * 判断vivo是否有刘海屏
     * https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20180328/20180328152252602.pdf
     *
     * @param activity
     * @return
     */
    private fun hasNotchVIVO(activity: Activity): Boolean {
        return try {
            val c = Class.forName("android.util.FtFeature")
            val get = c.getMethod("isFeatureSupport", Int::class.javaPrimitiveType)
            get.invoke(c, 0x20) as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断oppo是否有刘海屏
     * https://open.oppomobile.com/wiki/doc#id=10159
     *
     * @param activity
     * @return
     */
    private fun hasNotchOPPO(activity: Activity): Boolean {
        return activity.packageManager.hasSystemFeature("com.oppo.feature.screen.heteromorphism")
    }

    /**
     * 判断xiaomi是否有刘海屏
     * https://dev.mi.com/console/doc/detail?pId=1293
     *
     * @param activity
     * @return
     */
    private fun hasNotchXiaoMi(activity: Activity): Boolean {
        return try {
            val c = Class.forName("android.os.SystemProperties")
            val get = c.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)
            get.invoke(c, "ro.miui.notch", 0) as Int == 1
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 判断华为是否有刘海屏
     * https://devcenter-test.huawei.com/consumer/cn/devservice/doc/50114
     *
     * @param activity
     * @return
     */
    private fun hasNotchHw(activity: Activity): Boolean {
        return try {
            val cl = activity.classLoader
            val HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil")
            val get = HwNotchSizeUtil.getMethod("hasNotchInScreen")
            get.invoke(HwNotchSizeUtil) as Boolean
        } catch (e: Exception) {
            false
        }
    }
}