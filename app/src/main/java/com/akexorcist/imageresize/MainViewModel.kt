package com.akexorcist.imageresize

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.system.measureTimeMillis

class MainViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val FILE_NAME_PORTRAIT = "sample-portrait.jpg"
        private const val FILE_NAME_LANDSCAPE = "sample-landscape.jpg"
        private const val FILE_NAME_SQUARE = "sample-square.jpg"
        private const val IMAGE_RESIZE_COUNT = 30
        private const val IMAGE_RESIZE_STEP = 200
    }

    private val imageResizer = ImageResizer()
    private val fileUtils = FileUtils()
    private val uiMapper = MainUiMapper()
    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _uiModel = MutableLiveData<MainUiModel>()
    val uiModel: LiveData<MainUiModel> = _uiModel

    fun initSampleFile() = viewModelScope.launch(Dispatchers.IO) {
        listOf(
            FILE_NAME_LANDSCAPE,
            FILE_NAME_PORTRAIT,
            FILE_NAME_SQUARE
        ).forEach {
            initImageFile(it)
        }
        updateUiModel { uiMapper.onIdle() }
    }

    private fun initImageFile(fileName: String) {
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) {
            val inputStream = context.assets.open(fileName)
            fileUtils.copyInputStreamToFile(inputStream, file)
        }
    }

    fun startImageResize() = viewModelScope.launch(Dispatchers.IO) {
        val images = listOf(
            ImageRatio.Landscape to File(context.cacheDir, FILE_NAME_LANDSCAPE),
            ImageRatio.Portrait to File(context.cacheDir, FILE_NAME_PORTRAIT),
            ImageRatio.Square to File(context.cacheDir, FILE_NAME_SQUARE)
        )
        val totalImageResizeCount = IMAGE_RESIZE_COUNT * images.size
        _uiModel.postValue(uiMapper.onImageResizeStarted(totalImageResizeCount))
        images.forEach { (ratio, file) ->
            val maxImageSize = imageResizer.getMaxImageSize(file.absolutePath)
            (1..IMAGE_RESIZE_COUNT).forEach { index ->
                val preferredSize = IMAGE_RESIZE_STEP * index
                updateUiModel {
                    uiMapper.onImageResizeNext(
                    uiModel = _uiModel.value,
                        preferredSize = preferredSize,
                        ratio = ratio,
                        total = totalImageResizeCount
                    )
                }
                var bitmap: Bitmap?
                val millis = measureTimeMillis {
                    bitmap = imageResizer.resize(preferredSize, file.absolutePath)
                }
                val width = bitmap?.width ?: -1
                val height = bitmap?.height ?: -1
                bitmap?.recycle()
                updateUiModel {
                    uiMapper.onImageResizeStatusUpdated(
                        uiModel = _uiModel.value,
                        originalSize = maxImageSize,
                        preferredSize = preferredSize,
                        actualWidth = width,
                        actualHeight = height,
                        executionTime = millis,
                        ratio = ratio,
                        total = totalImageResizeCount
                    )
                }
            }
        }
        updateUiModel {
            uiMapper.onImageResizeCompleted(
                uiModel = _uiModel.value,
                total = totalImageResizeCount
            )
        }
    }

    fun retryImageResize() = viewModelScope.launch(Dispatchers.IO) {
        updateUiModel { uiMapper.onImageResizeRetry() }
        startImageResize()
    }

    private suspend fun updateUiModel(body: () -> MainUiModel) {
        withContext(Dispatchers.Main) {
            _uiModel.value = body.invoke()
        }
    }
}
