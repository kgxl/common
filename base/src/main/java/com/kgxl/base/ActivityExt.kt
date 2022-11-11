package com.kgxl.base

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Created by kgxl on 2022/11/10
 */
fun Activity.hasPermission(vararg permissions: String, isRequest: Boolean): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true
    val hasPermissions = mutableListOf<String>()
    for (permission in permissions) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            hasPermissions.add(permission)
        }
    }
    if (hasPermissions.isEmpty()) return true
    if (isRequest) {
        ActivityCompat.requestPermissions(this, hasPermissions.toTypedArray(), 100)
    }
    return false
}

fun Activity.canDownload(isRequest: Boolean): Boolean {
    return hasPermission(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        isRequest = isRequest
    )
}

fun Activity.canRecord(isRequest: Boolean): Boolean {
    return hasPermission(
        Manifest.permission.RECORD_AUDIO,
        isRequest = isRequest
    )
}

inline fun AppCompatActivity.launch(coroutineContext: EmptyCoroutineContext, crossinline invoke: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch(coroutineContext) {
        invoke()
    }
}

inline fun Fragment.launch(coroutineContext: EmptyCoroutineContext, crossinline invoke: suspend CoroutineScope.() -> Unit): Job {
    return lifecycleScope.launch(coroutineContext) {
        invoke()
    }
}

inline fun ViewModel.launch(coroutineContext: EmptyCoroutineContext, crossinline invoke: suspend CoroutineScope.() -> Unit): Job {
    return viewModelScope.launch(coroutineContext) {
        invoke()
    }
}

