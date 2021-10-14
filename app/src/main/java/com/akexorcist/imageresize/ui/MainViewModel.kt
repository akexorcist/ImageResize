package com.akexorcist.imageresize.ui

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.akexorcist.imageresize.vo.ImageRatio
import com.akexorcist.imageresize.resize.ImageResizer
import com.akexorcist.imageresize.utils.FileUtils
import com.akexorcist.imageresize.vo.MainUiModel
import com.akexorcist.imageresize.resize.ResizeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.system.measureTimeMillis

class MainViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val FILE_NAME_PORTRAIT = "sample-portrait.jpg"
        private const val FILE_NAME_LANDSCAPE = "sample-landscape.jpg"
        private const val FILE_NAME_SQUARE = "sample-square.jpg"
        private const val IMAGE_RESIZE_COUNT = 10
        private const val IMAGE_RESIZE_STEP = 400
    }

    private val imageResizer = ImageResizer()
    private val fileUtils = FileUtils()
    private val uiMapper = MainUiMapper()
    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val _uiModel = MutableLiveData<MainUiModel>()
    val uiModel: LiveData<MainUiModel> = _uiModel

    private var currentJob: Job? = null

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
        cancelActiveImageResize()
        currentJob = doImageResize()
    }

    fun cancelImageResize() = viewModelScope.launch {
        cancelActiveImageResize()
        updateUiModel { uiMapper.onCancel(_uiModel.value) }
    }

    private fun doImageResize() = viewModelScope.launch(Dispatchers.IO) {
        val images: List<Pair<ImageRatio, File>> = listOf(
            ImageRatio.Landscape to File(context.cacheDir, FILE_NAME_LANDSCAPE),
            ImageRatio.Portrait to File(context.cacheDir, FILE_NAME_PORTRAIT),
            ImageRatio.Square to File(context.cacheDir, FILE_NAME_SQUARE),
        )
        val resizeTypes = listOf(
            ResizeType.Fill,
            ResizeType.Crop
        )
        val initialSize = listOf(
            (IMAGE_RESIZE_STEP * 1.0f).toInt() to (IMAGE_RESIZE_STEP * 1.0f).toInt(),
            (IMAGE_RESIZE_STEP * 1.5f).toInt() to (IMAGE_RESIZE_STEP * 1.0f).toInt(),
            (IMAGE_RESIZE_STEP * 1.0f).toInt() to (IMAGE_RESIZE_STEP * 1.5f).toInt()
        )
        val totalImageResizeCount = IMAGE_RESIZE_COUNT * images.size * resizeTypes.size * initialSize.size
        val skipLargerResize = true
        _uiModel.postValue(uiMapper.onImageResizeStarted(totalImageResizeCount))
        images.forEach { (ratio, file) ->
            resizeTypes.forEach { resizeType ->
                initialSize.forEach { (initialWidth, initialHeight) ->
                    val (originalImageWidth, originalImageHeight) = imageResizer.getOriginalImageSize(file.absolutePath)
                    (1..IMAGE_RESIZE_COUNT).forEach { index ->
                        val preferredWidth = initialWidth * index
                        val preferredHeight = initialHeight * index
                        updateUiModel {
                            uiMapper.onImageResizeNext(
                                uiModel = _uiModel.value,
                                preferredWidth = preferredWidth,
                                preferredHeight = preferredHeight,
                                ratio = ratio,
                                total = totalImageResizeCount,
                                resizeType = resizeType
                            )
                        }
                        var bitmap: Bitmap?
                        val millis = measureTimeMillis {
                            bitmap = imageResizer.resize(
                                preferredWidth = preferredWidth,
                                preferredHeight = preferredHeight,
                                path = file.absolutePath,
                                resizeType = resizeType,
                                skipLargerResize = skipLargerResize
                            )
                        }
                        val width = bitmap?.width ?: -1
                        val height = bitmap?.height ?: -1
//                bitmap?.let { saveToStorage(it, preferredSize, ratio) }
                        bitmap?.recycle()
                        updateUiModel {
                            uiMapper.onImageResizeStatusUpdated(
                                uiModel = _uiModel.value,
                                originalWidth = originalImageWidth,
                                originalHeight = originalImageHeight,
                                preferredWidth = preferredWidth,
                                preferredHeight = preferredHeight,
                                actualWidth = width,
                                actualHeight = height,
                                executionTime = millis,
                                ratio = ratio,
                                resizeType = resizeType,
                                total = totalImageResizeCount,
                                skipLargerResize = skipLargerResize
                            )
                        }
                    }
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

    private fun cancelActiveImageResize() {
        if (currentJob?.isActive == true) {
            currentJob?.cancel()
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

    private fun saveToStorage(bitmap: Bitmap, preferredSize: Int, ratio: ImageRatio) {
        val type = when (ratio) {
            ImageRatio.Landscape -> "landscape"
            ImageRatio.Portrait -> "portrait"
            ImageRatio.Square -> "square"
        }
        val fileName = "${type}_$preferredSize.jpg"
        val file = File(context.filesDir, fileName)
        file.createNewFile()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(data)
        fileOutputStream.flush()
        fileOutputStream.close()
    }
}
