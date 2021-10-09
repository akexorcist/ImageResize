package com.akexorcist.imageresize

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class MainEvent: Parcelable {
    @Parcelize object OnIdle: MainEvent()
    @Parcelize object OnImageResizeRetry: MainEvent()
    @Parcelize object OnImageResizeStarted: MainEvent()
    @Parcelize object OnImageResizeNext: MainEvent()
    @Parcelize object OnImageResizeStatusUpdated: MainEvent()
    @Parcelize object OnImageResizeCompleted: MainEvent()
}
