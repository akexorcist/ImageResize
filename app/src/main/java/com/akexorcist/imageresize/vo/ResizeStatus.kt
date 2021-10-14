package com.akexorcist.imageresize.vo

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class ResizeStatus : Parcelable {
    @Parcelize object Pass : ResizeStatus()
    @Parcelize object Running : ResizeStatus()
    @Parcelize object Failed : ResizeStatus()
}
