package com.akexorcist.imageresize.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.imageresize.R
import com.akexorcist.imageresize.databinding.LayoutTestResultBinding
import com.akexorcist.imageresize.vo.ImageRatio
import com.akexorcist.imageresize.utils.NumberUtils
import com.akexorcist.imageresize.vo.ResizeStatus
import com.akexorcist.imageresize.resize.ResizeType
import com.akexorcist.imageresize.vo.TestResult

class TestResultViewHolder(
    private val binding: LayoutTestResultBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val numberUtils = NumberUtils()
    private val context: Context
        get() = binding.root.context

    fun bind(result: TestResult) {
        updateTitle(result.ratio, result.resizeType)
        updatePreferredSize(result.preferredWidth, result.preferredHeight)
        updateActualSize(result.actualWidth, result.actualHeight)
        updateExecutionTime(result.executionTime)
        updateStatus(result.status)
    }

    private fun updateTitle(imageRatio: ImageRatio, resizeType: ResizeType) {
        val imageRatioText = when (imageRatio) {
            ImageRatio.Landscape -> context.getString(R.string.ratio_landscape)
            ImageRatio.Portrait -> context.getString(R.string.ratio_portrait)
            ImageRatio.Square -> context.getString(R.string.ratio_square)
        }
        val resizeTypeText = when (resizeType) {
            ResizeType.Fill -> context.getString(R.string.resize_type_fill)
            ResizeType.Crop -> context.getString(R.string.resize_type_crop)
        }
        binding.textViewTitle.text = context.getString(
            R.string.result_title,
            imageRatioText,
            resizeTypeText
        )
    }

    private fun updatePreferredSize(preferredWidth: Int, preferredHeight: Int) {
        binding.textViewPreferredSize.text = context.getString(
            R.string.result_preferred_size,
            numberUtils.toDisplayNumber(preferredWidth),
            numberUtils.toDisplayNumber(preferredHeight)
        )
    }

    private fun updateActualSize(width: Int, height: Int) {
        binding.textViewActualSize.text =
            if (width > 0 && height > 0) context.getString(
                R.string.actual_size,
                numberUtils.toDisplayNumber(width),
                numberUtils.toDisplayNumber(height)
            )
            else context.getString(R.string.unknown)
    }

    private fun updateExecutionTime(executionTime: Long) {
        binding.textViewExecutionTime.text =
            if (executionTime > 0) executionTime.toString()
            else context.getString(R.string.unknown)
    }

    private fun updateStatus(status: ResizeStatus) {
        when (status) {
            ResizeStatus.Pass -> {
                binding.viewStatus.isSelected = true
                binding.viewStatus.isEnabled = true
            }
            ResizeStatus.Failed -> {
                binding.viewStatus.isSelected = false
                binding.viewStatus.isEnabled = false
            }
            ResizeStatus.Running -> {
                binding.viewStatus.isSelected = false
                binding.viewStatus.isEnabled = true
            }
        }
    }
}
