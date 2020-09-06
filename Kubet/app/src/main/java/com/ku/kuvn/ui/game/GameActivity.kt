package com.ku.kuvn.ui.game

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityGameBinding
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.utils.MetricsUtil

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel
    private val adapter = GamePagerAdapter()

    private var onPageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.tvTitle.setText(R.string.game_title)
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        val pageMargin = resources.getDimensionPixelSize(R.dimen.game_item_margin)
        val pageTranslationX = pageMargin + resources.getDimensionPixelSize(R.dimen.game_item_space)
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
        }

        binding.vpGame.setPageTransformer(pageTransformer)
        binding.vpGame.offscreenPageLimit = 1
        binding.vpGame.addItemDecoration(HorizontalMarginItemDecoration(pageMargin))
        binding.vpGame.adapter = adapter

        adapter.clickHandler = { game ->
            if (game != null) {
                startActivity(GameDetailActivity.getIntent(this, game.link))
            }
        }

        onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val item = adapter.getItem(position)
                if (item != null) {
                    binding.tvGameTitle.post {
                        binding.tvGameTitle.text = item.name
                    }
                }
            }
        }
        binding.vpGame.registerOnPageChangeCallback(onPageChangeCallback!!)

        binding.btnPlay.setOnClickListener {
            val game = adapter.getItem(binding.vpGame.currentItem)
            if (game != null) {
                startActivity(GameDetailActivity.getIntent(this, game.link))
            }
        }

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get()
        viewModel.getShowLoadingLiveData().observe(this, Observer {
            binding.progressBar.visibility = if (it == true) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        })
        viewModel.getNetworkErrorLiveData().observe(this, Observer { error ->
            if (error != null) {
                setNetworkErrorVisible(error)
            }
        })
        viewModel.getGamesLiveData().observe(this, Observer {
            binding.layoutContent.visibility = View.VISIBLE
            adapter.addData(it)
        })
        viewModel.init()
    }

    private fun setNetworkErrorVisible(error: DisplayableError) {
        if (error.visible) {
            if (binding.layoutError.root.visibility != View.VISIBLE) {
                binding.layoutError.root.visibility = View.VISIBLE
                binding.layoutError.tvTitle.setText(R.string.network_issue_title)
                binding.layoutError.tvMessage.setText(R.string.network_issue_message)

                if (error.action != null) {
                    binding.layoutError.btnRetry.visibility = View.VISIBLE
                    binding.layoutError.btnRetry.setOnClickListener { error.action.invoke() }
                } else {
                    binding.layoutError.btnRetry.visibility = View.INVISIBLE
                }
            }
        } else {
            binding.layoutError.root.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onPageChangeCallback?.let { binding.vpGame.unregisterOnPageChangeCallback(it) }
    }

    private class HorizontalMarginItemDecoration(private val horizontalMarginInPx: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                    state: RecyclerView.State) {
            outRect.left = horizontalMarginInPx
            outRect.right = horizontalMarginInPx
        }
    }
}