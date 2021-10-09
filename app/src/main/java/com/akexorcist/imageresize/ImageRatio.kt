package com.akexorcist.imageresize

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ImageRatio: Parcelable {
    @Parcelize object Landscape: ImageRatio()
    @Parcelize object Portrait: ImageRatio()
    @Parcelize object Square: ImageRatio()
}
