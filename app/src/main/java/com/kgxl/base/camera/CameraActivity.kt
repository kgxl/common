package com.kgxl.base.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.ImageFormat.*
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.hjq.toast.ToastUtils
import com.kgxl.base.App
import com.kgxl.base.BaseBindingActivity
import com.kgxl.base.ext.launch
import com.kgxl.base.test.databinding.ActivityCameraBinding
import com.kgxl.qrcode.ZxingUtils
import kotlinx.coroutines.Dispatchers
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*


class CameraActivity : BaseBindingActivity<ActivityCameraBinding>() {
    override fun initViewBinding(): ActivityCameraBinding {
        return ActivityCameraBinding.inflate(layoutInflater)
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            } else {
                realInit()
            }
        } else {
            realInit()
        }
    }

    override fun initData() {
        mViewBinding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }
        mViewBinding.videoCaptureButton.setOnClickListener {

        }
    }

    private fun takePhoto() {
        //构建一个uri
        val name =
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, name)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputFileOptions =
            ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()
        imageCapture?.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let {
                    val c = this@CameraActivity.contentResolver.query(it, null, null, null, null)
                    c?.let {
                        if (c.moveToFirst()) {
                            val data =
                                c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                            ToastUtils.show("保存路径： ${data}")
                        }
                        c.close()
                    }
                }
            }

            override fun onError(exception: ImageCaptureException) {
                ToastUtils.show("保存失败 $exception")
            }
        })
    }

    private fun takeVideo() {

    }

    override fun initObserver() {
        App.application.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, PhotoContentObserve(this, Handler()))
    }

    var imageCapture: ImageCapture? = null
    var videoCapture: VideoCapture? = null
    private fun realInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val listenableFuture = ProcessCameraProvider.getInstance(this)
            listenableFuture.addListener({
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(mViewBinding.viewFinder.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    val provider = listenableFuture.get()
                    imageCapture = ImageCapture.Builder().build()
                    val imageAnalysis =
                        ImageAnalysis.Builder().setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST).build().also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
                                if (image.getFormat() === YUV_420_888 || image.getFormat() === YUV_422_888 || image.getFormat() === YUV_444_888) {
                                    val byteBuffer: ByteBuffer =
                                        image.planes[0].buffer
                                    val imageData = ByteArray(byteBuffer.capacity())
                                    byteBuffer.get(imageData)
//
                                    launch(Dispatchers.IO) {
                                        ZxingUtils.decodeQrcode(imageData, image.width, image.height)
                                    }
                                }
                                image.close()
                            }
                        }
                    provider.unbindAll()
                    provider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis)
                } catch (e: Exception) {

                }
            }, ContextCompat.getMainExecutor(this))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            permissions.forEachIndexed { index, s ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    realInit()
                }
            }
        }
    }

}