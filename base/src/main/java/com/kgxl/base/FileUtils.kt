package com.kgxl.base

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import android.util.Log
import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


/**
 * Created by zjy on 2022/11/11
 */
class FileUtils {
    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    fun getFileByPath(filePath: String): File? {
        return if (isSpace(filePath)) null else File(filePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            true
        } else isFileExists(file.getAbsolutePath())
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String): Boolean {
        val file: File = getFileByPath(filePath) ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath)
    }

    private fun isFileExistsApi29(filePath: String?): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri: Uri = Uri.parse(filePath)
                val cr = Utils.getApp().contentResolver
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }

    /**
     * Rename the file.
     *
     * @param filePath The path of file.
     * @param newName  The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(filePath: String, newName: String): Boolean {
        return rename(getFileByPath(filePath), newName)
    }

    /**
     * Rename the file.
     *
     * @param file    The file.
     * @param newName The new name of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun rename(file: File?, newName: String): Boolean {
        // file is null then return false
        if (file == null) return false
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (isSpace(newName)) return false
        // the new name equals old name then return true
        if (newName == file.getName()) return true
        val newFile = File(file.getParent() + File.separator.toString() + newName)
        // the new name of file exists then return false
        return (!newFile.exists()
                && file.renameTo(newFile))
    }

    /**
     * Return whether it is a directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(dirPath: String): Boolean {
        return isDir(getFileByPath(dirPath))
    }

    /**
     * Return whether it is a directory.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory()
    }

    /**
     * Return whether it is a file.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(filePath: String): Boolean {
        return isFile(getFileByPath(filePath))
    }

    /**
     * Return whether it is a file.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile()
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(dirPath: String): Boolean {
        return createOrExistsDir(getFileByPath(dirPath))
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory() else file.mkdirs()
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param filePath The path of file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(filePath: String): Boolean {
        return createOrExistsFile(getFileByPath(filePath))
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile()
        return if (!createOrExistsDir(file.getParentFile())) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(filePath: String): Boolean {
        return createFileByDeleteOldFile(getFileByPath(filePath))
    }

    /**
     * Create a file if it doesn't exist, otherwise delete old file before creating.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        return if (!createOrExistsDir(file.getParentFile())) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Copy the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(srcPath: String,
             destPath: String): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * Copy the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(srcPath: String,
             destPath: String,
             listener: OnReplaceListener?): Boolean {
        return copy(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }

    /**
     * Copy the directory or file.
     *
     * @param src  The source.
     * @param dest The destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(src: File?,
             dest: File): Boolean {
        return copy(src, dest, null)
    }

    /**
     * Copy the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copy(src: File?,
             dest: File?,
             listener: OnReplaceListener?): Boolean {
        if (src == null) return false
        if (dest == null) return false
        return if (src.isDirectory()) {
            copyDir(src, dest, listener)
        } else copyFile(src, dest, listener)
    }

    /**
     * Copy the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyDir(srcDir: File,
                        destDir: File,
                        listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, false)
    }

    /**
     * Copy the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun copyFile(srcFile: File,
                         destFile: File,
                         listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, false)
    }

    /**
     * Move the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(srcPath: String,
             destPath: String): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), null)
    }

    /**
     * Move the directory or file.
     *
     * @param srcPath  The path of source.
     * @param destPath The path of destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(srcPath: String,
             destPath: String,
             listener: OnReplaceListener?): Boolean {
        return move(getFileByPath(srcPath), getFileByPath(destPath), listener)
    }

    /**
     * Move the directory or file.
     *
     * @param src  The source.
     * @param dest The destination.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(src: File?,
             dest: File?): Boolean {
        return move(src, dest, null)
    }

    /**
     * Move the directory or file.
     *
     * @param src      The source.
     * @param dest     The destination.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun move(src: File?,
             dest: File?,
             listener: OnReplaceListener?): Boolean {
        if (src == null) return false
        return if (src.isDirectory()) {
            moveDir(src, dest, listener)
        } else moveFile(src, dest, listener)
    }

    /**
     * Move the directory.
     *
     * @param srcDir   The source directory.
     * @param destDir  The destination directory.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveDir(srcDir: File?,
                destDir: File?,
                listener: OnReplaceListener?): Boolean {
        return copyOrMoveDir(srcDir, destDir, listener, true)
    }

    /**
     * Move the file.
     *
     * @param srcFile  The source file.
     * @param destFile The destination file.
     * @param listener The replace listener.
     * @return `true`: success<br></br>`false`: fail
     */
    fun moveFile(srcFile: File?,
                 destFile: File?,
                 listener: OnReplaceListener?): Boolean {
        return copyOrMoveFile(srcFile, destFile, listener, true)
    }

    private fun copyOrMoveDir(srcDir: File?,
                              destDir: File?,
                              listener: OnReplaceListener?,
                              isMove: Boolean): Boolean {
        if (srcDir == null || destDir == null) return false
        // destDir's path locate in srcDir's path then return false
        val srcPath: String = srcDir.getPath() + File.separator
        val destPath: String = destDir.getPath() + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory()) return false
        if (!createOrExistsDir(destDir)) return false
        val files: Array<File> = srcDir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                val oneDestFile = File(destPath + file.getName())
                if (file.isFile()) {
                    if (!copyOrMoveFile(file, oneDestFile, listener, isMove)) return false
                } else if (file.isDirectory()) {
                    if (!copyOrMoveDir(file, oneDestFile, listener, isMove)) return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(srcFile: File?,
                               destFile: File?,
                               listener: OnReplaceListener?,
                               isMove: Boolean): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile.equals(destFile)) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile()) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(srcFile, destFile)) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createOrExistsDir(destFile.getParentFile())) false else try {
            (writeFileFromIS(File(destFile.getAbsolutePath()), FileInputStream(srcFile),false)
                    && !(isMove && !deleteFile(srcFile)))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete the directory.
     *
     * @param filePath The path of file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(filePath: String): Boolean {
        return delete(getFileByPath(filePath))
    }

    /**
     * Delete the directory.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory()) {
            deleteDir(file)
        } else deleteFile(file)
    }

    /**
     * Delete the directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false
        val files: Array<File> = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (file.isFile()) {
                    if (!file.delete()) return false
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * Delete the file.
     *
     * @param file The file.
     * @return `true`: success<br></br>`false`: fail
     */
    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile() && file.delete())
    }

    /**
     * Delete the all in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dirPath: String): Boolean {
        return deleteAllInDir(getFileByPath(dirPath))
    }

    /**
     * Delete the all in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { true }
    }

    /**
     * Delete all files in directory.
     *
     * @param dirPath The path of directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dirPath: String): Boolean {
        return deleteFilesInDir(getFileByPath(dirPath))
    }

    /**
     * Delete all files in directory.
     *
     * @param dir The directory.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir) { pathname -> pathname.isFile() }
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dirPath: String,
                                   filter: FileFilter?): Boolean {
        return deleteFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * Delete all files that satisfy the filter in directory.
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return `true`: success<br></br>`false`: fail
     */
    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter?): Boolean {
        if (dir == null || filter == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false
        val files: Array<File> = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String): List<File?>? {
        return listFilesInDir(dirPath, null)
    }

    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir The directory.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?): List<File?>? {
        return listFilesInDir(dir, null)
    }

    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath    The path of directory.
     * @param comparator The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String, comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDir(getFileByPath(dirPath), false, comparator)
    }

    /**
     * Return the files in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir        The directory.
     * @param comparator The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?, comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDir(dir, false, comparator)
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String, isRecursive: Boolean): List<File?>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive)
    }

    /**
     * Return the files in directory.
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?, isRecursive: Boolean): List<File?>? {
        return listFilesInDir(dir, isRecursive, null)
    }

    /**
     * Return the files in directory.
     *
     * @param dirPath     The path of directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dirPath: String,
                       isRecursive: Boolean,
                       comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDir(getFileByPath(dirPath), isRecursive, comparator)
    }

    /**
     * Return the files in directory.
     *
     * @param dir         The directory.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files in directory
     */
    fun listFilesInDir(dir: File?,
                       isRecursive: Boolean,
                       comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDirWithFilter(dir, object : FileFilter {
            override fun accept(pathname: File): Boolean {
                return true
            }
        }, isRecursive, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath The path of directory.
     * @param filter  The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dirPath: String,
                                 filter: FileFilter): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir    The directory.
     * @param filter The filter.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dir: File?,
                                 filter: FileFilter): List<File?>? {
        return listFilesInDirWithFilter(dir, filter, false, null)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dirPath    The path of directory.
     * @param filter     The filter.
     * @param comparator The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dirPath: String,
                                 filter: FileFilter,
                                 comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * Doesn't traverse subdirectories
     *
     * @param dir        The directory.
     * @param filter     The filter.
     * @param comparator The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dir: File?,
                                 filter: FileFilter,
                                 comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDirWithFilter(dir, filter, false, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dirPath: String,
                                 filter: FileFilter,
                                 isRecursive: Boolean): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dir: File?,
                                 filter: FileFilter,
                                 isRecursive: Boolean): List<File?>? {
        return listFilesInDirWithFilter(dir, filter, isRecursive, null)
    }


    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dirPath     The path of directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dirPath: String,
                                 filter: FileFilter,
                                 isRecursive: Boolean,
                                 comparator: Comparator<File?>?): List<File?>? {
        return listFilesInDirWithFilter(getFileByPath(dirPath), filter, isRecursive, comparator)
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    fun listFilesInDirWithFilter(dir: File?,
                                 filter: FileFilter,
                                 isRecursive: Boolean,
                                 comparator: Comparator<File?>?): List<File>? {
        val files: List<File> = listFilesInDirWithFilterInner(dir, filter, isRecursive)
        if (comparator != null) {
            Collections.sort(files, comparator)
        }
        return files
    }

    private fun listFilesInDirWithFilterInner(dir: File?,
                                              filter: FileFilter,
                                              isRecursive: Boolean): List<File> {
        val list: MutableList<File> = ArrayList()
        if (!isDir(dir)) return list
        val files: Array<File> = dir?.listFiles() ?: arrayOf<File>()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (filter.accept(file)) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory()) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true))
                }
            }
        }
        return list
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param filePath The path of file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(filePath: String): Long {
        return getFileLastModified(getFileByPath(filePath))
    }

    /**
     * Return the time that the file was last modified.
     *
     * @param file The file.
     * @return the time that the file was last modified
     */
    fun getFileLastModified(file: File?): Long {
        return if (file == null) -1 else file.lastModified()
    }

    /**
     * Return the number of lines of file.
     *
     * @param filePath The path of file.
     * @return the number of lines of file
     */
    fun getFileLines(filePath: String): Int {
        return getFileLines(getFileByPath(filePath))
    }

    /**
     * Return the number of lines of file.
     *
     * @param file The file.
     * @return the number of lines of file
     */
    fun getFileLines(file: File?): Int {
        var count = 1
        var `is`: InputStream? = null
        try {
            `is` = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP.endsWith("\n")) {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\n'.code.toByte()) ++count
                    }
                }
            } else {
                while (`is`.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.code.toByte()) ++count
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (`is` != null) {
                    `is`.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return count
    }

    /**
     * Return the size.
     *
     * @param filePath The path of file.
     * @return the size
     */
    fun getSize(filePath: String): String? {
        return getSize(getFileByPath(filePath))
    }

    /**
     * Return the size.
     *
     * @param file The directory.
     * @return the size
     */
    fun getSize(file: File?): String? {
        if (file == null) return ""
        return if (file.isDirectory()) {
            getDirSize(file)
        } else getFileSize(file)
    }

    /**
     * Return the size of directory.
     *
     * @param dir The directory.
     * @return the size of directory
     */
    private fun getDirSize(dir: File): String? {
        val len = getDirLength(dir)
        return if (len == -1L) "" else byte2FitMemorySize(len, 3)
    }

    /**
     * Return the size of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileSize(file: File): String? {
        val len: Long = getFileLength(file)
        return if (len == -1L) "" else byte2FitMemorySize(len, 3)
    }

    /**
     * Return the length.
     *
     * @param filePath The path of file.
     * @return the length
     */
    fun getLength(filePath: String): Long {
        return getLength(getFileByPath(filePath))
    }

    /**
     * Return the length.
     *
     * @param file The file.
     * @return the length
     */
    fun getLength(file: File?): Long {
        if (file == null) return 0
        return if (file.isDirectory()) {
            getDirLength(file)
        } else getFileLength(file)
    }

    /**
     * Return the length of directory.
     *
     * @param dir The directory.
     * @return the length of directory
     */
    private fun getDirLength(dir: File): Long {
        if (!isDir(dir)) return 0
        var len: Long = 0
        val files: Array<File> = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (file.isDirectory()) {
                    len += getDirLength(file)
                } else {
                    len += file.length()
                }
            }
        }
        return len
    }

    /**
     * Return the length of file.
     *
     * @param file The file.
     * @return the length of file
     */
    private fun getFileLength(file: File): Long {
        return if (!isFile(file)) -1 else file.length()
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(filePath: String): String? {
        val file: File? = if (isSpace(filePath)) null else File(filePath)
        return getFileMD5ToString(file)
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5ToString(file: File?): String {
        return bytes2HexString(getFileMD5(file), true)
    }

    /**
     * Return the MD5 of file.
     *
     * @param filePath The path of file.
     * @return the md5 of file
     */
    fun getFileMD5(filePath: String): ByteArray? {
        return getFileMD5(getFileByPath(filePath))
    }

    /**
     * Return the MD5 of file.
     *
     * @param file The file.
     * @return the md5 of file
     */
    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md: MessageDigest = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.getMessageDigest()
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (dis != null) {
                    dis.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * Return the file's path of directory.
     *
     * @param file The file.
     * @return the file's path of directory
     */
    fun getDirName(file: File?): String? {
        return if (file == null) "" else getDirName(file.getAbsolutePath())
    }

    /**
     * Return the file's path of directory.
     *
     * @param filePath The path of file.
     * @return the file's path of directory
     */
    fun getDirName(filePath: String): String? {
        if (isSpace(filePath)) return ""
        val lastSep: Int = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    /**
     * Return the name of file.
     *
     * @param file The file.
     * @return the name of file
     */
    fun getFileName(file: File?): String? {
        return if (file == null) "" else getFileName(file.getAbsolutePath())
    }

    /**
     * Return the name of file.
     *
     * @param filePath The path of file.
     * @return the name of file
     */
    fun getFileName(filePath: String): String? {
        if (isSpace(filePath)) return ""
        val lastSep: Int = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    /**
     * Return the name of file without extension.
     *
     * @param file The file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(file: File?): String? {
        return if (file == null) "" else getFileNameNoExtension(file.getPath())
    }

    /**
     * Return the name of file without extension.
     *
     * @param filePath The path of file.
     * @return the name of file without extension
     */
    fun getFileNameNoExtension(filePath: String): String? {
        if (isSpace(filePath)) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep: Int = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    /**
     * Return the extension of file.
     *
     * @param file The file.
     * @return the extension of file
     */
    fun getFileExtension(file: File?): String? {
        return if (file == null) "" else getFileExtension(file.getPath())
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    fun getFileExtension(filePath: String): String? {
        if (isSpace(filePath)) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep: Int = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }

    /**
     * Notify system to scan the file.
     *
     * @param filePath The path of file.
     */
    fun notifySystemToScan(filePath: String) {
        notifySystemToScan(getFileByPath(filePath))
    }

    /**
     * Notify system to scan the file.
     *
     * @param file The file.
     */
    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) return
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.parse("file://" + file.getAbsolutePath())
        Utils.getApp().sendBroadcast(intent)
    }

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val totalSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            totalSize = statFs.blockCountLong
        } else {
            blockSize = statFs.blockSize.toLong()
            totalSize = statFs.blockCount.toLong()
        }
        return blockSize * totalSize
    }

    /**
     * Return the available size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the available size of file system
     */
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val availableSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            availableSize = statFs.availableBlocksLong
        } else {
            blockSize = statFs.blockSize.toLong()
            availableSize = statFs.availableBlocks.toLong()
        }
        return blockSize * availableSize
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnReplaceListener {
        fun onReplace(srcFile: File?, destFile: File?): Boolean
    }

    private fun isSpace(s: String): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

    private fun bytes2HexString(bytes: ByteArray?, isUpperCase: Boolean): String {
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

    private val HEX_DIGITS_UPPER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private val HEX_DIGITS_LOWER =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    val BYTE = 1
    val KB = 1024
    val MB = 1048576
    val GB = 1073741824

    private val sBufferSize = 524288

    private val LINE_SEP = System.getProperty("line.separator")

    private fun byte2FitMemorySize(byteSize: Long, precision: Int): String {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < MB) {
            java.lang.String.format("%." + precision + "fKB", byteSize.toDouble() / KB)
        } else if (byteSize < GB) {
            java.lang.String.format("%." + precision + "fMB", byteSize.toDouble() / MB)
        } else {
            java.lang.String.format("%." + precision + "fGB", byteSize.toDouble() / GB)
        }
    }

    fun writeFileFromIS(file: File,
                        `is`: InputStream?,
                        append: Boolean): Boolean {
        if (`is` == null || !createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var os: OutputStream? = null
        return try {
            os = BufferedOutputStream(FileOutputStream(file, append), sBufferSize)
            val data = ByteArray(sBufferSize)
            var len: Int
            while (`is`.read(data).also { len = it } != -1) {
                os.write(data, 0, len)
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            try {
                os?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}