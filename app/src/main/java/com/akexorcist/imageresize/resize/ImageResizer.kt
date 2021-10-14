package com.akexorcist.imageresize.resize

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageResizer {
    fun resize(
        preferredSize: Int,
        path: String,
        resizeType: ResizeType,
        skipLargerResize: Boolean = true,
    ): Bitmap? {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            inSampleSize = calculateInSampleSize(this, preferredSize, preferredSize)
            inJustDecodeBounds = false
            val expectSize = when (resizeType) {
                ResizeType.Fill -> outWidth.coerceAtLeast(outHeight)
                ResizeType.Crop -> outWidth.coerceAtMost(outHeight)
            }
            if (!skipLargerResize || expectSize > preferredSize) {
                inDensity = expectSize
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

    fun getMinImageSize(path: String): Int {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            outWidth.coerceAtMost(outHeight)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (width: Int, height: Int) = options.run { outWidth to outHeight }
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
