package com.kgxl.base.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*

/**
 * Created by kgxl on 2023/1/10
 */
class BitmapUtils {
    /**
     * android 11及以上保存图片到相册
     * @return 图片路径 可以为null
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToGallery(context: Context, suffix: String, resource: File): String? {
        val time = System.currentTimeMillis()
        val fileName = "$suffix$time.jpg"
        val values = ContentValues()
        val path = Environment.DIRECTORY_PICTURES + File.separator + context.packageName
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, path)
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        values.put(MediaStore.MediaColumns.IS_PENDING, false)
        val resolver = context.contentResolver
        val insertUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: return null
        var inputStream: BufferedInputStream? = null
        var os: OutputStream? = null
        return try {
            inputStream = BufferedInputStream(FileInputStream(resource.absolutePath))
            os = resolver.openOutputStream(insertUri)
            if (os != null) {
                val buffer = ByteArray(1024 * 4)
                var len: Int
                while (inputStream.read(buffer).also { len = it } != -1) {
                    os.write(buffer, 0, len)
                }
                os.flush()
            }
            path + File.separator + fileName
        } catch (e: IOException) {
            null
        } finally {
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun saveImage2(context: Context, srcFile: File, targetFile: File, localSavePath: String) =
        withContext(Dispatchers.IO) {
            val resultIsSuccess = FileUtils.copy(srcFile, targetFile)
            if (resultIsSuccess) {
                //保存成功处理，通知图库更新
                val intent = Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://$localSavePath")
                )
                context.sendBroadcast(intent)
                return@withContext targetFile.absolutePath
            } else {
                //保存失败处理
                return@withContext null
            }
        }

    suspend fun saveImage(context: Context,
                          localSavePath: String,
                          srcFile: File,
                          suffix: String): String? = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return@withContext saveImageToGallery(context, suffix, srcFile)
        } else {
            val targetFile = File(localSavePath)
            return@withContext saveImage2(context, srcFile, targetFile, localSavePath)
        }
    }
}


