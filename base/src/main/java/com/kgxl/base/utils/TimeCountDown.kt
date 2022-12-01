package com.kgxl.base.utils

/**
 * Created by zjy on 2022/11/28
 */

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 *  Created by zjy on 2021/8/27.
 *  基于[android.os.CountDownTimer] 新增[pause] [resume]
 */
class CountDownTimerWrapper(
    /**
     * end time.
     */
    var endTime: Long = System.currentTimeMillis(),
    /**
     * The interval in millis that the user receives callbacks
     */
    private var mCountdownInterval: Long = 1000L
) : LifecycleObserver {

    val handlerThread by lazy { HandlerThread(javaClass.simpleName) }

    init {
        handlerThread.start()
    }

    private var mStopTimeInFuture: Long = 0
    private var mMillisInFuture: Long = 0


    /**
     * boolean representing if the timer was cancelled
     */
    private var mCancelled = false

    private var mPaused = false

    private var mStarted = false

    /**
     * Cancel the countdown.
     */
    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancel() {
        mCancelled = true
        mStarted = false
        mHandler.removeCallbacksAndMessages(null)
        handlerThread.looper?.quit()
    }

    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cancelNotQuit() {
        mCancelled = true
        mStarted = false
        mHandler.removeCallbacksAndMessages(null)
    }

    /**
     * Pause the countdown.
     */
    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pause() {
        mPaused = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        mPaused = true
    }

    /**
     * Start the countdown.
     */
    @Synchronized
    fun start() {
        if (mStarted || mPaused) {
            return
        }
        mCancelled = false
        mStarted = true
        mMillisInFuture = endTime - System.currentTimeMillis()
        realStart()
    }

    /**
     * Resume the countdown.
     */
    @Synchronized
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        if (mCancelled) {
            return
        }
        if (mPaused) {
            mPaused = false
            mMillisInFuture = endTime - System.currentTimeMillis()
            realStart()
        }
    }

    private fun realStart() {
        if (mMillisInFuture <= 0) {
            onCountDownListener?.onFinish()
        } else {
            mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture
            mHandler.sendMessage(mHandler.obtainMessage(MSG))
        }
    }

    var onCountDownListener: OnCountDownTimerListener? = null
    fun setOnCountDownTimerListener(onCountDownTimerListener: OnCountDownTimerListener) {
        this.onCountDownListener = onCountDownTimerListener
    }

    interface OnCountDownTimerListener {
        /**
         * Callback fired on regular interval.
         * @param millisUntilFinished The amount of time until finished.
         */
        fun onTick(millisUntilFinished: Long)

        /**
         * Callback fired when the time is up.
         */
        fun onFinish()
    }

    // handles counting down
    private val mHandler: Handler = object : Handler(handlerThread.looper) {
        override fun handleMessage(msg: Message) {
            synchronized(this) {
                if (mCancelled || mPaused) {
                    return
                }
                val millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime()
                if (millisLeft <= 0) {
                    onCountDownListener?.onFinish()
                } else {
                    val lastTickStart = SystemClock.elapsedRealtime()
                    onCountDownListener?.onTick(millisLeft)

                    // take into account user's onTick taking time to execute
                    val lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart
                    var delay: Long
                    if (millisLeft < mCountdownInterval) {
                        // just delay until done
                        delay = millisLeft - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, trigger onFinish without delay
                        if (delay < 0) delay = 0
                    } else {
                        delay = mCountdownInterval - lastTickDuration

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval
                    }
                    sendMessageDelayed(obtainMessage(MSG), delay)
                }
            }
        }
    }

    companion object {
        private const val MSG = 1
    }
}