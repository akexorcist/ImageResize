package com.akexorcist.imageresize

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.imageresize.databinding.LayoutTestResultBinding

class TestResultViewHolder(
    private val binding: LayoutTestResultBinding
) : RecyclerView.ViewHolder(binding.root) {
    private val numberUtils = NumberUtils()
    private val context: Context
        get() = binding.root.context

    fun bind(result: TestResult) {
        updateTitle(result.ratio, result.preferredSize)
        updateActualSize(result.actualWidth, result.actualHeight)
        updateExecutionTime(result.executionTime)
        updateStatus(result.status)
    }

    private fun updateTitle(ratio: ImageRatio, preferredSize: Int) {
        val type = when (ratio) {
            ImageRatio.Landscape -> context.getString(R.string.ratio_landscape)
            ImageRatio.Portrait -> context.getString(R.string.ratio_portrait)
            ImageRatio.Square -> context.getString(R.string.ratio_square)
        }
        binding.textViewTitle.text = context.getString(
            R.string.result_title,
            type,
            numberUtils.toDisplayNumber(preferredSize)
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
