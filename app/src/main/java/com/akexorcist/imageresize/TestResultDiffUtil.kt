package com.akexorcist.imageresize

import androidx.recyclerview.widget.DiffUtil

class TestResultDiffUtil(
    private val oldItem: List<TestResult>,
    private val newItem: List<TestResult>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem.getOrNull(oldItemPosition)?.preferredSize ==
                newItem.getOrNull(newItemPosition)?.preferredSize &&
                oldItem.getOrNull(oldItemPosition)?.ratio ==
                newItem.getOrNull(newItemPosition)?.ratio &&
                oldItem.getOrNull(oldItemPosition)?.resizeType ==
                newItem.getOrNull(newItemPosition)?.resizeType
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItem.getOrNull(oldItemPosition)?.actualWidth ==
                newItem.getOrNull(newItemPosition)?.actualWidth &&
                oldItem.getOrNull(oldItemPosition)?.actualHeight ==
                newItem.getOrNull(newItemPosition)?.actualHeight &&
                oldItem.getOrNull(oldItemPosition)?.executionTime ==
                newItem.getOrNull(newItemPosition)?.executionTime &&
                oldItem.getOrNull(oldItemPosition)?.status ==
                newItem.getOrNull(newItemPosition)?.status
    }

    override fun getOldListSize() = oldItem.size

    override fun getNewListSize() = newItem.size
}
