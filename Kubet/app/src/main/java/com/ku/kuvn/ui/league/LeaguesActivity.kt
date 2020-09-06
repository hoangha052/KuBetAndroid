package com.ku.kuvn.ui.league

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
import com.ku.kuvn.databinding.ActivityLeaguesBinding
import com.ku.kuvn.ui.DisplayableError

class LeaguesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaguesBinding
    private lateinit var viewModel: LeaguesViewModel
    private val leagueAdapter = LeaguesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaguesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.tvTitle.setText(R.string.sport_title)
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        binding.rcLeagues.setHasFixedSize(true)
        binding.rcLeagues.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcLeagues.addItemDecoration(LeagueItemDecoration(resources.getDimensionPixelOffset(R.dimen.league_item_offset)))
        binding.rcLeagues.adapter = leagueAdapter

        leagueAdapter.clickHandler = { league ->
            if (league != null) {
                startActivity(LeagueDetailActivity.getIntent(this, league.id, league.name))
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
        viewModel.getLeaguesLiveData().observe(this, Observer {
            leagueAdapter.addData(it)
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

    private class LeagueItemDecoration(private val offset: Int) : RecyclerView.ItemDecoration() {

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