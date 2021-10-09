package com.akexorcist.imageresize

import java.io.*
import java.lang.Exception

class FileUtils {
    fun copyInputStreamToFile(`in`: InputStream, file: File) {
        var out: OutputStream? = null
        try {
            out = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                out?.close()
                `in`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}