package com.akexorcist.imageresize.ui

import com.akexorcist.imageresize.vo.ImageRatio
import com.akexorcist.imageresize.resize.ResizeType
import com.akexorcist.imageresize.vo.*

class MainUiMapper {
    fun onIdle(): MainUiModel {
        return MainUiModel(
            event = MainEvent.OnIdle,
            results = listOf(),
            totalImageResizeCount = 0
        )
    }

    fun onCancel(uiModel: MainUiModel?): MainUiModel {
        val results = uiModel?.results ?: listOf()
        return MainUiModel(
            event = MainEvent.OnCancel,
            results = results,
            totalImageResizeCount = uiModel?.totalImageResizeCount ?: 0
        )
    }

    fun onImageResizeStarted(total: Int): MainUiModel {
        return MainUiModel(
            event = MainEvent.OnImageResizeStarted,
            results = listOf(),
            totalImageResizeCount = total
        )
    }

    fun onImageResizeNext(
        uiModel: MainUiModel?,
        preferredWidth: Int,
        preferredHeight: Int,
        total: Int,
        ratio: ImageRatio,
        resizeType: ResizeType
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        val addedResults = results.toMutableList().apply {
            add(
                TestResult(
                    preferredWidth = preferredWidth,
                    preferredHeight = preferredHeight,
                    actualWidth = -1,
                    actualHeight = -1,
                    executionTime = -1,
                    status = ResizeStatus.Running,
                    ratio = ratio,
                    resizeType = resizeType
                )
            )
        }
        return MainUiModel(
            event = MainEvent.OnImageResizeNext,
            results = addedResults,
            totalImageResizeCount = total
        )
    }

    fun onImageResizeStatusUpdated(
        uiModel: MainUiModel?,
        originalWidth: Int,
        originalHeight: Int,
        preferredWidth: Int,
        preferredHeight: Int,
        actualWidth: Int,
        actualHeight: Int,
        executionTime: Long,
        ratio: ImageRatio,
        resizeType: ResizeType,
        total: Int,
        skipLargerResize: Boolean
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        val state = when {
            resizeType == ResizeType.Fill &&
                    (actualWidth == preferredWidth || actualHeight == preferredHeight) &&
                    (actualWidth <= preferredWidth && actualHeight <= preferredHeight) ||
                    (skipLargerResize && actualWidth == originalWidth && actualHeight == originalHeight) ->
                ResizeStatus.Pass
            resizeType == ResizeType.Crop &&
                    (actualWidth == preferredWidth || actualHeight == preferredHeight) &&
                    (actualWidth >= preferredWidth && actualHeight >= preferredHeight) ||
                    (skipLargerResize && actualWidth == originalWidth && actualHeight == originalHeight) ->
                ResizeStatus.Pass
            else -> ResizeStatus.Failed
        }
        val updatedResults = results.map {
            if (preferredWidth == it.preferredWidth &&
                preferredHeight == it.preferredHeight &&
                ratio == it.ratio &&
                resizeType == it.resizeType
            ) TestResult(
                preferredWidth = preferredWidth,
                preferredHeight = preferredHeight,
                actualWidth = actualWidth,
                actualHeight = actualHeight,
                executionTime = executionTime,
                status = state,
                ratio = ratio,
                resizeType = resizeType
            )
            else it
        }
        return MainUiModel(
            event = MainEvent.OnImageResizeStatusUpdated,
            results = updatedResults,
            totalImageResizeCount = total
        )
    }

    fun onImageResizeCompleted(
        uiModel: MainUiModel?,
        total: Int
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        return MainUiModel(
            event = MainEvent.OnImageResizeCompleted,
            results = results,
            totalImageResizeCount = total
        )
    }

    fun onImageResizeRetry(): MainUiModel {
        return MainUiModel(
            event = MainEvent.OnImageResizeRetry,
            results = listOf(),
            totalImageResizeCount = 0
        )
    }
}
