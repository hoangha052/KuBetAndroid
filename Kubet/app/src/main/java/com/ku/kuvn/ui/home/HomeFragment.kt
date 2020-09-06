package com.ku.kuvn.ui.home

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ku.kuvn.KuApplication
import com.ku.kuvn.R
import com.ku.kuvn.api.Menu
import com.ku.kuvn.databinding.FragmentHomeBinding
import com.ku.kuvn.delegate.EndlessRecyclerViewScrollListener
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.ui.game.GameActivity
import com.ku.kuvn.ui.league.LeaguesActivity
import com.ku.kuvn.utils.openMenu

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val homeAdapter = HomeAdapter()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val gridLayoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (position) {
                        1, 2 -> 1
                        else -> 2
                    }
                }
            }
        }

        binding.rcMain.setHasFixedSize(true)
        binding.rcMain.layoutManager = gridLayoutManager
        binding.rcMain.addItemDecoration(
                MainItemDecoration(resources.getDimensionPixelOffset(R.dimen.main_item_offset)))
        binding.rcMain.adapter = homeAdapter
        binding.rcMain.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                viewModel.loadMore()
            }
        })

        homeAdapter.bannerClickHandler = { banner ->
            if (banner != null) {
                val ownerActivity = activity
                if (ownerActivity != null) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.url))
                    if (intent.resolveActivity(ownerActivity.packageManager) != null) {
                        ownerActivity.startActivity(intent)
                    }
                }
            }
        }
        homeAdapter.gameClickHandler = {
            startActivity(Intent(activity, GameActivity::class.java))
        }
        homeAdapter.sportClickHandler = {
            startActivity(Intent(activity, LeaguesActivity::class.java))
        }
        homeAdapter.menuClickHandler = { menu ->
            openMenu(menu)
        }

        viewModel = ViewModelProvider(this,
                HomeViewModel.Factory(activity!!.application as KuApplication)).get()
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
        viewModel.getHomeItemsLiveData().observe(viewLifecycleOwner, Observer {
            homeAdapter.addData(it)
        })
        viewModel.init()

        lifecycle.addObserver(homeAdapter)

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

    private fun openMenu(menu: Menu) {
        val ownerActivity = activity
        ownerActivity ?: return
        openMenu(ownerActivity, menu)
    }

    class MainItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect,
                                    view: View,
                                    parent: RecyclerView,
                                    state: RecyclerView.State) {
            val childAdapterPosition = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter?.itemCount?.dec() ?: 0
            when (childAdapterPosition) {
                0 -> outRect.set(offset, offset, offset, offset / 2)
                1 -> outRect.set(offset, offset / 2, offset / 2, offset / 2)
                2 -> outRect.set(offset / 2, offset / 2, offset, offset / 2)
                itemCount -> outRect.set(offset, offset / 2, offset, offset)
                else -> outRect.set(offset, offset / 2, offset, offset / 2)
            }
        }
    }
}