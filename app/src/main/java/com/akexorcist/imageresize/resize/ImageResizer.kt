package com.akexorcist.imageresize.resize

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class ImageResizer {
    fun resize(
        preferredWidth: Int,
        preferredHeight: Int,
        path: String,
        resizeType: ResizeType,
        skipLargerResize: Boolean = true,
    ): Bitmap? {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            inSampleSize = calculateInSampleSize(this, preferredWidth, preferredHeight)
            inJustDecodeBounds = false
            if (!skipLargerResize || preferredWidth <= outWidth && preferredHeight <= outHeight) {
                val outRatio = outWidth.toFloat() / outHeight.toFloat()
                val reqRatio = preferredWidth.toFloat() / preferredHeight.toFloat()
                when {
                    (resizeType == ResizeType.Fill && outRatio > reqRatio) ||
                            (resizeType == ResizeType.Crop && outRatio <= reqRatio) -> {
                        inDensity = outWidth
                        inTargetDensity = preferredWidth * inSampleSize
                    }
                    (resizeType == ResizeType.Fill && outRatio <= reqRatio) ||
                            (resizeType == ResizeType.Crop && outRatio > reqRatio) -> {
                        inDensity = outHeight
                        inTargetDensity = preferredHeight * inSampleSize
                    }
                }
            }
            BitmapFactory.decodeFile(path, this)
        }
    }

    fun getOriginalImageSize(path: String): Pair<Int, Int> {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, this)
            outWidth to outHeight
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
