package com.akexorcist.imageresize.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.imageresize.*
import com.akexorcist.imageresize.databinding.ActivityMainBinding
import com.akexorcist.imageresize.ui.adapter.TestResultAdapter
import com.akexorcist.imageresize.ui.adapter.TestResultDiffUtil
import com.akexorcist.imageresize.vo.MainEvent
import com.akexorcist.imageresize.vo.MainUiModel
import com.akexorcist.imageresize.vo.TestResult

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private val adapter: TestResultAdapter by lazy {
        TestResultAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.buttonStart.setOnClickListener { viewModel.startImageResize() }
        binding.buttonTryAgain.setOnClickListener { viewModel.retryImageResize() }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        viewModel.uiModel.observe(this) { updateUiModel(it) }
        viewModel.initSampleFile()
    }

    private fun updateUiModel(uiModel: MainUiModel) {
        when (uiModel.event) {
            MainEvent.OnIdle -> {
                binding.buttonStart.visibility = View.VISIBLE
                binding.buttonTryAgain.visibility = View.GONE
            }
            MainEvent.OnImageResizeStarted -> {
                binding.buttonStart.isEnabled = false
                binding.buttonStart.text = getString(R.string.start)
                binding.buttonStart.visibility = View.VISIBLE
                binding.buttonTryAgain.visibility = View.GONE
            }
            MainEvent.OnImageResizeNext -> {
                updateTestResults(uiModel.results)
                binding.buttonStart.text = getString(
                    R.string.image_resize_count,
                    uiModel.results.size,
                    uiModel.totalImageResizeCount
                )
                binding.textViewInstruction.visibility = View.GONE
            }
            MainEvent.OnImageResizeStatusUpdated -> {
                updateTestResults(uiModel.results)
                binding.buttonStart.text = getString(
                    R.string.image_resize_count,
                    uiModel.results.size,
                    uiModel.totalImageResizeCount
                )
            }
            MainEvent.OnImageResizeCompleted -> {
                updateTestResults(uiModel.results)
                binding.buttonStart.visibility = View.GONE
                binding.buttonTryAgain.visibility = View.VISIBLE
            }
            MainEvent.OnImageResizeRetry -> {
                updateTestResults(uiModel.results)
            }
        }
    }

    private fun updateTestResults(newResults: List<TestResult>) {
        val oldResults = adapter.getTestResults()
        val diffResult = DiffUtil.calculateDiff(TestResultDiffUtil(oldResults, newResults))
        adapter.updateTestResults(newResults)
        diffResult.dispatchUpdatesTo(adapter)
        if (adapter.itemCount != 0) {
            binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
        }
    }
}
