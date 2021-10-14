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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.system.measureTimeMillis

class MainViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val FILE_NAME_PORTRAIT = "sample-portrait.jpg"
        private const val FILE_NAME_LANDSCAPE = "sample-landscape.jpg"
        private const val FILE_NAME_SQUARE = "sample-square.jpg"
        private const val IMAGE_RESIZE_COUNT = 20
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
        val images: List<Triple<ImageRatio, File, ResizeType>> = listOf(
            Triple(ImageRatio.Landscape, File(context.cacheDir, FILE_NAME_LANDSCAPE), ResizeType.Fill),
            Triple(ImageRatio.Portrait, File(context.cacheDir, FILE_NAME_PORTRAIT), ResizeType.Fill),
            Triple(ImageRatio.Square, File(context.cacheDir, FILE_NAME_SQUARE), ResizeType.Fill),
            Triple(ImageRatio.Landscape, File(context.cacheDir, FILE_NAME_LANDSCAPE), ResizeType.Crop),
            Triple(ImageRatio.Portrait, File(context.cacheDir, FILE_NAME_PORTRAIT), ResizeType.Crop),
            Triple(ImageRatio.Square, File(context.cacheDir, FILE_NAME_SQUARE), ResizeType.Crop),
        )
        val totalImageResizeCount = IMAGE_RESIZE_COUNT * images.size
        val skipLargerResize = false
        _uiModel.postValue(uiMapper.onImageResizeStarted(totalImageResizeCount))
        images.forEach { (ratio, file, resizeType) ->
            val originalImageSize = when (resizeType) {
                ResizeType.Fill -> imageResizer.getMaxImageSize(file.absolutePath)
                ResizeType.Crop -> imageResizer.getMinImageSize(file.absolutePath)
            }
            (1..IMAGE_RESIZE_COUNT).forEach { index ->
                val preferredSize = IMAGE_RESIZE_STEP * index
                updateUiModel {
                    uiMapper.onImageResizeNext(
                        uiModel = _uiModel.value,
                        preferredSize = preferredSize,
                        ratio = ratio,
                        total = totalImageResizeCount,
                        resizeType = resizeType
                    )
                }
                var bitmap: Bitmap?
                val millis = measureTimeMillis {
                    bitmap = imageResizer.resize(
                        preferredSize = preferredSize,
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
                        originalSize = originalImageSize,
                        preferredSize = preferredSize,
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
