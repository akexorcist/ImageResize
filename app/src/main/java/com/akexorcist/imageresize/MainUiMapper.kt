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
        ratio: ImageRatio
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
                    ratio = ratio
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
        total: Int
    ): MainUiModel {
        val results = uiModel?.results ?: listOf()
        val maxActualSize = actualWidth.coerceAtLeast(actualHeight)
        val expectSize = originalSize.coerceAtMost(preferredSize)
        val state =
            if (maxActualSize == expectSize) ResizeStatus.Pass
            else ResizeStatus.Failed
        val updatedResults = results.map {
            if (preferredSize == it.preferredSize && ratio == it.ratio) TestResult(
                preferredSize = preferredSize,
                actualWidth = actualWidth,
                actualHeight = actualHeight,
                executionTime = executionTime,
                status = state,
                ratio = ratio
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
