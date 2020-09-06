package com.ku.kuvn.ui.gift

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityBlogsCategoryBinding
import com.ku.kuvn.delegate.EndlessRecyclerViewScrollListener
import com.ku.kuvn.ui.DisplayableError

class BlogsByCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlogsCategoryBinding
    private lateinit var viewModel: BlogsByCategoryViewModel
    private val giftAdapter = GiftAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlogsCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title") ?: ""

        binding.defaultTopBar.tvTitle.text = title
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        binding.rcBlogs.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcBlogs.layoutManager = linearLayoutManager
        binding.rcBlogs.addItemDecoration(GifItemDecoration(
                resources.getDimensionPixelOffset(R.dimen.gift_item_offset)))
        binding.rcBlogs.adapter = giftAdapter
        binding.rcBlogs.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                viewModel.loadMore()
            }
        })

        giftAdapter.clickHandler = { gift ->
            if (gift != null) {
                startActivity(GiftDetailActivity.getIntent(this, gift.id))
            }
        }

        val id = intent.getStringExtra("id") ?: ""

        viewModel = ViewModelProvider(this, BlogsByCategoryViewModel.Factory(application, id)).get()
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
        viewModel.getGiftsLiveData().observe(this, Observer {
            giftAdapter.addData(it)
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

    private class GifItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                    state: RecyclerView.State) {
            val childAdapterPosition = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount?.dec() ?: 0
            when (childAdapterPosition) {
                0 -> outRect.set(offset, offset, offset, offset / 2)
                itemCount -> outRect.set(offset, offset / 2, offset, offset)
                else -> outRect.set(offset, offset / 2, offset, offset / 2)
            }
        }
    }

    companion object {

        fun getIntent(context: Context, title: String, id: String): Intent {
            val intent = Intent(context, BlogsByCategoryActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("id", id)
            return intent
        }
    }
}