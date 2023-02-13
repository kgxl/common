package com.kgxl.base.ml

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.os.Build
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.kgxl.base.BaseBindingActivity
import com.kgxl.base.ext.launch
import com.kgxl.base.test.databinding.ActivityQrcodeBinding
import com.kgxl.qrcode.ZxingUtils
import kotlinx.coroutines.Dispatchers
import java.nio.ByteBuffer

class QrcodeActivity : BaseBindingActivity<ActivityQrcodeBinding>() {
    override fun initViewBinding(): ActivityQrcodeBinding {
        return ActivityQrcodeBinding.inflate(layoutInflater)
    }

    override fun initView() {
        mViewBinding.btnCreate.setOnClickListener {
            val bmp = ZxingUtils.createQRImage("test qrcode", 200, 200, true)
            mViewBinding.ivQrcode.setImageBitmap(bmp)
        }
        mViewBinding.scan.setOnClickListener {
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
    }

    override fun initData() {
    }

    override fun initObserver() {
    }

    var imageCapture: ImageCapture? = null
    private fun realInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val listenableFuture = ProcessCameraProvider.getInstance(this)
            listenableFuture.addListener({
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(mViewBinding.preview.surfaceProvider)
                }
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    val provider = listenableFuture.get()
                    imageCapture = ImageCapture.Builder().build()
                    val imageAnalysis =
                        ImageAnalysis.Builder().setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                            it.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
                                if (image.getFormat() === ImageFormat.YUV_420_888 || image.getFormat() === ImageFormat.YUV_422_888 || image.getFormat() === ImageFormat.YUV_444_888) {
                                    val byteBuffer: ByteBuffer =
                                        image.planes[0].buffer
                                    val imageData = ByteArray(byteBuffer.capacity())
                                    byteBuffer.get(imageData)
                                    launch(Dispatchers.IO) {
                                        val text = ZxingUtils.decodeQrcode(imageData, image.width, image.height)
                                        mViewBinding.tvScanResult.text = "扫描结果：${text}"
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

}