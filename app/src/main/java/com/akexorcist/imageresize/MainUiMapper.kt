package com.akexorcist.imageresize

class MainUiMapper {
    fun onIdle(): MainUiModel {
        return MainUiModel(
            event = MainEvent.OnIdle,
            results = listOf(),
            totalImageResizeCount = 0
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
        preferredSize: Int,
        total: Int,
        ratio: ImageRatio,
        resizeType: ResizeType
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        val addedResults = results.toMutableList().apply {
            add(
                TestResult(
                    preferredSize = preferredSize,
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
        originalSize: Int,
        preferredSize: Int,
        actualWidth: Int,
        actualHeight: Int,
        executionTime: Long,
        ratio: ImageRatio,
        resizeType: ResizeType,
        total: Int,
        skipLargerResize: Boolean
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        val maxActualSize = actualWidth.coerceAtLeast(actualHeight)
        val minActualSize = actualWidth.coerceAtMost(actualHeight)
        val expectSize =
            if(skipLargerResize) originalSize.coerceAtMost(preferredSize)
            else preferredSize
        val state = when {
            resizeType == ResizeType.Fill && maxActualSize == expectSize -> ResizeStatus.Pass
            resizeType == ResizeType.Crop && minActualSize == expectSize -> ResizeStatus.Pass
            else -> ResizeStatus.Failed
        }
        val updatedResults = results.map {
            if (preferredSize == it.preferredSize &&
                ratio == it.ratio &&
                resizeType == it.resizeType
            ) TestResult(
                preferredSize = preferredSize,
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
