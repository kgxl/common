package com.kgxl.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.*


/**
 * Created by zjy on 2023/2/1
 */
object ZxingUtils {
    fun createQRImage(content: String?, widthPix: Int, heightPix: Int, isDeleteWhite: Boolean): Bitmap? {
        var widthPix = widthPix
        var heightPix = heightPix
        return try {
            val hints: Hashtable<EncodeHintType, Any?> = Hashtable()
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8")
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            hints.put(EncodeHintType.MARGIN, if (isDeleteWhite) 1 else 0)
            var matrix =
                QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix, heightPix, hints)
            if (isDeleteWhite) {
                //删除白边
                matrix = deleteWhite(matrix)
            }
            widthPix = matrix.width
            heightPix = matrix.height
            val pixels = IntArray(widthPix * heightPix)
            for (y in 0 until heightPix) {
                for (x in 0 until widthPix) {
                    if (matrix[x, y]) {
                        pixels[y * widthPix + x] = Color.BLACK
                    } else {
                        pixels[y * widthPix + x] = Color.WHITE
                    }
                }
            }
            val bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix)
            bitmap
        } catch (e: Exception) {
            null
        }
    }


    /**
     * 删除白色边框
     *
     * @param matrix matrix
     * @return BitMatrix
     */
    private fun deleteWhite(matrix: BitMatrix): BitMatrix {
        val rec = matrix.enclosingRectangle
        val resWidth = rec[2] + 1
        val resHeight = rec[3] + 1
        val resMatrix = BitMatrix(resWidth, resHeight)
        resMatrix.clear()
        for (i in 0 until resWidth) {
            for (j in 0 until resHeight) {
                if (matrix[i + rec[0], j + rec[1]]) resMatrix[i] = j
            }
        }
        return resMatrix
    }

    suspend fun decodeQrcode(imageData: ByteArray, width: Int, height: Int): String {
        val source =
            PlanarYUVLuminanceSource(imageData, width, height,
                0, 0, width, height, false)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val decode = MultiFormatReader().apply {
                val hints = Hashtable<DecodeHintType, Any>()
                val decodeFormats = Vector<BarcodeFormat>()
                decodeFormats.add(BarcodeFormat.QR_CODE)
                decodeFormats.add(BarcodeFormat.CODABAR)
                hints[DecodeHintType.POSSIBLE_FORMATS] = decodeFormats
                hints[DecodeHintType.CHARACTER_SET] = "UTF-8"
                this.setHints(hints)
            }.decode(binaryBitmap)
            return decode.text
        } catch (e: FormatException) {
        } catch (e: ChecksumException) {
        } catch (e: NotFoundException) {
        }
        return ""
    }
}