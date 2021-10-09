package com.akexorcist.imageresize

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageResizer {
    fun resize(preferredSize: Int, path: String): Bitmap? {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            inSampleSize = calculateInSampleSize(this, preferredSize, preferredSize)
            inJustDecodeBounds = false
            val maxImageSize = outWidth.coerceAtLeast(outHeight)
            if (maxImageSize > preferredSize) {
                inDensity = maxImageSize
                inTargetDensity = preferredSize * inSampleSize
            }
            BitmapFactory.decodeFile(path, this)
        }
    }

    fun getMaxImageSize(path: String): Int {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            outWidth.coerceAtLeast(outHeight)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
