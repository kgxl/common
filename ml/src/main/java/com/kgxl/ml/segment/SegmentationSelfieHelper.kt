package com.kgxl.ml.segment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import java.nio.ByteBuffer

/**
 * Created by zjy on 2023/1/30
 */
object SegmentationSelfieHelper {

    fun startCamera() {
        val options = getCameraOptions()
        val segmentation = Segmentation.getClient(options)

    }

    fun startImage(bitmap: Bitmap, result: MutableLiveData<Bitmap>) {
        val options = getImageOptions()
        val segmentation = Segmentation.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        segmentation.process(inputImage).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it.buffer.array(), 0, it.buffer.capacity())
            result.value = bitmap
        }.addOnFailureListener {
            result.value = null
        }
    }

    fun startImage(ctx: Context, bitmap: Uri, result: MutableLiveData<Bitmap>) {
        val options = getImageOptions()
        val segmentation = Segmentation.getClient(options)
        val inputImage = InputImage.fromFilePath(ctx, bitmap)
        segmentation.process(inputImage).addOnSuccessListener {
            val colors = maskColorsFromByteBuffer(it.buffer,it.width,it.height)
            val bitmap =
                Bitmap.createBitmap(colors, it.width, it.height, Bitmap.Config.ARGB_8888)
            result.value = bitmap
        }.addOnFailureListener {
            result.value = null
        }
    }

    @ColorInt
    private fun maskColorsFromByteBuffer(byteBuffer: ByteBuffer, maskWidth: Int, maskHeight: Int): IntArray {
        @ColorInt val colors =
            IntArray(maskWidth * maskHeight)
        for (i in 0 until maskWidth * maskHeight) {
            val backgroundLikelihood = 1 - byteBuffer.float
            if (backgroundLikelihood > 0.9) {
                colors[i] = Color.argb(128, 255, 0, 255)
            } else if (backgroundLikelihood > 0.2) {
                // Linear interpolation to make sure when backgroundLikelihood is 0.2, the alpha is 0 and
                // when backgroundLikelihood is 0.9, the alpha is 128.
                // +0.5 to round the float value to the nearest int.
                val alpha = (182.9 * backgroundLikelihood - 36.6 + 0.5).toInt()
                colors[i] = Color.argb(alpha, 255, 0, 255)
            }
        }
        return colors
    }


    private fun getImageOptions(): SelfieSegmenterOptions {
        return SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE).enableRawSizeMask().build()
    }

    private fun getCameraOptions(): SelfieSegmenterOptions {
        return SelfieSegmenterOptions.Builder().setDetectorMode(SelfieSegmenterOptions.STREAM_MODE).enableRawSizeMask().build()
    }
}