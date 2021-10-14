package com.akexorcist.imageresize.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.imageresize.databinding.LayoutTestResultBinding
import com.akexorcist.imageresize.vo.TestResult

class TestResultAdapter : RecyclerView.Adapter<TestResultViewHolder>() {
    private var results: List<TestResult> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestResultViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutTestResultBinding.inflate(inflater, parent, false)
        return TestResultViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestResultViewHolder, position: Int) {
        results.getOrNull(position)?.let { result ->
            holder.bind(result)
        }
    }

    override fun getItemCount(): Int = results.size

    fun updateTestResults(results: List<TestResult>) {
        this.results = results
    }

    fun getTestResults() = results
}
