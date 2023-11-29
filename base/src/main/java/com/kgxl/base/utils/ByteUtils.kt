package com.kgxl.base.utils

import android.util.Base64
import com.kgxl.base.Utils
import java.util.*

/**
 * Created by kgxl on 2022/11/14
 */
object ByteUtils {
    fun bytes2HexString(bytes: ByteArray?, isUpperCase: Boolean = true): String {
        if (bytes == null) return ""
        val hexDigits: CharArray = if (isUpperCase) HEX_DIGITS_UPPER else HEX_DIGITS_LOWER
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[(bytes[i].toInt() shr 4) and 0x0f]
            ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }

    /**
     * Hex string to bytes.
     *
     * e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString The hex string.
     * @return the bytes
     */
    fun hexString2Bytes(hexString: String): ByteArray {
        var hexStr = hexString
        if (Utils.isSpace(hexString)) return ByteArray(0)
        var len = hexStr.length
        if (len % 2 != 0) {
            hexStr = "0$hexStr"
            len += 1
        }
        val hexBytes = hexStr.uppercase(Locale.getDefault()).toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }

    private fun hex2Dec(hexChar: Char): Int {
        return when (hexChar) {
            in '0'..'9' -> {
                hexChar - '0'
            }
            in 'A'..'F' -> {
                hexChar - 'A' + 10
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }


    fun base64Encode(input: ByteArray?): ByteArray? {
        return if (input == null || input.isEmpty()) ByteArray(0) else Base64.encode(input, Base64.NO_WRAP)
    }

    /**
     * Return the bytes of decode Base64-encode string.
     *
     * @param input The input.
     * @return the string of decode Base64-encode string
     */
    fun base64Decode(input: String?): ByteArray? {
        return if (input == null || input.isEmpty()) ByteArray(0) else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Return the bytes of decode Base64-encode bytes.
     *
     * @param input The input.
     * @return the bytes of decode Base64-encode bytes
     */
    fun base64Decode(input: ByteArray?): ByteArray? {
        return if (input == null || input.isEmpty()) ByteArray(0) else Base64.decode(input, Base64.NO_WRAP)
    }

    /**
     * Return Base64-encode string.
     *
     * @param input The input.
     * @return Base64-encode string
     */
    fun base64Encode2String(input: ByteArray?): String? {
        return if (input == null || input.isEmpty()) "" else Base64.encodeToString(input, Base64.NO_WRAP)
    }

    private val HEX_DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_DIGITS_LOWER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
}