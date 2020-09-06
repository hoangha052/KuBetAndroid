package com.ku.kuvn.ui.gift

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ku.kuvn.R
import com.ku.kuvn.databinding.FragmentGiftBinding
import com.ku.kuvn.delegate.EndlessRecyclerViewScrollListener
import com.ku.kuvn.ui.DisplayableError

class GiftFragment : Fragment() {

    private lateinit var binding: FragmentGiftBinding
    private lateinit var viewModel: GiftViewModel
    private val giftAdapter = GiftAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentGiftBinding.inflate(layoutInflater)

        binding.rcGift.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rcGift.layoutManager = linearLayoutManager
        binding.rcGift.addItemDecoration(GifItemDecoration(resources.getDimensionPixelOffset(R.dimen.gift_item_offset)))
        binding.rcGift.adapter = giftAdapter
        binding.rcGift.addOnScrollListener(object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                viewModel.loadMore()
            }
        })

        giftAdapter.clickHandler = { gift ->
            val ownerActivity = activity
            if (gift != null && ownerActivity != null) {
                startActivity(GiftDetailActivity.getIntent(ownerActivity, gift.id))
            }
        }

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(activity!!.application)).get()
        viewModel.getShowLoadingLiveData().observe(viewLifecycleOwner, Observer {
            binding.progressBar.visibility = if (it == true) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        })
        viewModel.getNetworkErrorLiveData().observe(viewLifecycleOwner, Observer { error ->
            if (error != null) {
                setNetworkErrorVisible(error)
            }
        })
        viewModel.getGiftsLiveData().observe(viewLifecycleOwner, Observer {
            giftAdapter.addData(it)
        })
        viewModel.init()
        return binding.root
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
}