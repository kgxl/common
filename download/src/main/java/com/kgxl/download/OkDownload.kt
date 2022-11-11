package com.kgxl.download

import com.liulishuo.okdownload.DownloadContext
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.breakpoint.BlockInfo
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener3
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend


/**
 * Created by zjy on 2022/11/11
 */
class OkDownload private constructor() : IDownload<DownloadTask> {

    companion object {
        fun getInstance(): OkDownload {
            return OkDownloadHolder.download
        }
    }

    private object OkDownloadHolder {
        var download: OkDownload = OkDownload()
    }

    override fun init() {
    }

    override fun resume(id: String) {
    }

    override fun resumeAll() {
    }

    override fun pause(id: String) {
    }

    override fun pauseAll() {
    }

    override fun cancel(id: String) {
    }

    override fun cancelAll() {
    }

    override fun start(task: DownloadTask, isSerial: Boolean, listener: DownloadListener<DownloadTask>) {
        task.enqueue(object : DownloadListener3() {
//            override fun taskStart(task: DownloadTask) {
//                listener.onStart(task)
//            }
//
//            override fun connectStart(task: DownloadTask, blockIndex: Int, requestHeaderFields: MutableMap<String, MutableList<String>>) {
//            }
//
//            override fun connectEnd(task: DownloadTask, blockIndex: Int, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {
//            }

            override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
                println("retry")

            }

            override fun connected(task: DownloadTask, blockCount: Int, currentOffset: Long, totalLength: Long) {
                println("connected")

            }

            override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
                println("progress ${currentOffset} ${totalLength}")

            }

            override fun started(task: DownloadTask) {
                println("started ")
            }

            override fun completed(task: DownloadTask) {
                println("completed ")

            }

            override fun canceled(task: DownloadTask) {
                println("canceled ")
            }

            override fun error(task: DownloadTask, e: java.lang.Exception) {
                println("error ${e.toString()}")
            }

            override fun warn(task: DownloadTask) {
                println("warn")
            }

//            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?, taskSpeed: SpeedCalculator) {
//                if (cause == EndCause.COMPLETED) {
//                    listener.onComplete(task)
//                } else {
//                    listener.onError(task,realCause.toString())
//                }
//            }
//
//            override fun infoReady(task: DownloadTask, info: BreakpointInfo, fromBreakpoint: Boolean, model: Listener4SpeedAssistExtend.Listener4SpeedModel) {
//            }
//
//            override fun progressBlock(task: DownloadTask, blockIndex: Int, currentBlockOffset: Long, blockSpeed: SpeedCalculator) {
//            }
//
//            override fun progress(task: DownloadTask, currentOffset: Long, taskSpeed: SpeedCalculator) {
//                (listener as OKDownloadListener).onOkProgress(task, currentOffset, taskSpeed)
//            }
//
//            override fun blockEnd(task: DownloadTask, blockIndex: Int, info: BlockInfo?, blockSpeed: SpeedCalculator) {
//            }

        })
    }

    override fun startMulti(tasks: List<DownloadTask>, isSerial: Boolean, listener: DownloadListener<DownloadTask>) {
        val set = DownloadContext.QueueSet()
        val commit = set.commit()
        commit.build().start(object : DownloadListener4WithSpeed() {
            override fun taskStart(task: DownloadTask) {
                listener.onStart(task)
            }

            override fun connectStart(task: DownloadTask, blockIndex: Int, requestHeaderFields: MutableMap<String, MutableList<String>>) {
            }

            override fun connectEnd(task: DownloadTask, blockIndex: Int, responseCode: Int, responseHeaderFields: MutableMap<String, MutableList<String>>) {
            }

            override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?, taskSpeed: SpeedCalculator) {
                if (cause == EndCause.COMPLETED) {
                    listener.onComplete(task)
                } else {
                    listener.onError(task,realCause.toString())
                }
            }

            override fun infoReady(task: DownloadTask, info: BreakpointInfo, fromBreakpoint: Boolean, model: Listener4SpeedAssistExtend.Listener4SpeedModel) {
            }

            override fun progressBlock(task: DownloadTask, blockIndex: Int, currentBlockOffset: Long, blockSpeed: SpeedCalculator) {
            }

            override fun progress(task: DownloadTask, currentOffset: Long, taskSpeed: SpeedCalculator) {
                (listener as OKDownloadListener).onOkProgress(task, currentOffset, taskSpeed)
            }

            override fun blockEnd(task: DownloadTask, blockIndex: Int, info: BlockInfo?, blockSpeed: SpeedCalculator) {
            }

        }, isSerial)
    }

    override fun createTask(url: String, targetPath: String): DownloadTask {
        return DownloadTask.Builder(url, targetPath, url.substring(url.lastIndexOf("/"), url.length))
            // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(30)
            .setConnectionCount(1)
            // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(false)
            .build()
    }
}