package com.ku.kuvn.ui.league

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityLeagueDetailBinding
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.ui.league.result.MatchResultActivity
import com.ku.kuvn.ui.league.schedule.MatchScheduleActivity
import com.ku.kuvn.ui.league.standings.StandingItemDecoration
import com.ku.kuvn.ui.league.standings.StandingsActivity
import com.ku.kuvn.ui.league.standings.StandingsAdapter
import com.ku.kuvn.utils.MetricsUtil
import com.ku.kuvn.utils.date.getDateWithServerTimeStamp
import java.text.SimpleDateFormat
import java.util.*

class LeagueDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeagueDetailBinding
    private lateinit var viewModel: LeagueViewModel
    private lateinit var adapter: StandingsAdapter

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy '-' hh:mm aa", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeagueDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")

        binding.defaultTopBar.tvTitle.text = name
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        val id = intent.getStringExtra("id") ?: ""

        binding.clickableView.setOnClickListener {
            if (name != null) {
                startActivity(StandingsActivity.getIntent(this, id, name))
            }
        }
        binding.layoutSchedule.root.setOnClickListener {
            if (name != null) {
                startActivity(MatchScheduleActivity.getIntent(this, id, name))
            }
        }
        binding.layoutResult.root.setOnClickListener {
            if (name != null) {
                startActivity(MatchResultActivity.getIntent(this, id, name))
            }
        }

        adapter = StandingsAdapter()
        binding.rcStandings.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false)
        binding.rcStandings.addItemDecoration(StandingItemDecoration(
                MetricsUtil.convertDpToPixel(24f, this).toInt(),
                ContextCompat.getDrawable(this, R.drawable.bg_divider)))
        binding.rcStandings.adapter = adapter

        viewModel = ViewModelProvider(this, LeagueViewModel.Factory(application, id)).get()
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
        viewModel.getStandingTeamsLiveData().observe(this, Observer {
            binding.layoutStandings.visibility = View.VISIBLE
            adapter.addData(it)
        })

        viewModel.getScheduleMatchLiveData().observe(this, Observer {
            binding.layoutSchedule.root.visibility = View.VISIBLE
            binding.layoutSchedule.tvTitle.text = "Lịch Thi Đấu"

            val date = it.time.getDateWithServerTimeStamp()
            if (date != null) {
                binding.layoutSchedule.tvDate.text = dateFormat.format(date)
            }
            binding.layoutSchedule.tvTeamName1.text = it.home.name
            binding.layoutSchedule.tvTeamName2.text = it.away.name

            Glide.with(this)
                .load(it.home.logo)
                .into(binding.layoutSchedule.imgTeam1)

            Glide.with(this)
                .load(it.away.logo)
                .into(binding.layoutSchedule.imgTeam2)
        })

        viewModel.getResultMatchLiveData().observe(this, Observer {
            binding.layoutResult.root.visibility = View.VISIBLE
            binding.layoutResult.tvTitle.text = "Kết Quả"

            val date = it.time.getDateWithServerTimeStamp()
            if (date != null) {
                binding.layoutResult.tvDate.text = dateFormat.format(date)
            }
            binding.layoutResult.tvTeamName1.text = it.home.name
            binding.layoutResult.tvTeamName2.text = it.away.name
            binding.layoutResult.tvScore.text = "%d - %d".format(Locale.US, it.homeScore, it.awayScore)

            Glide.with(this)
                .load(it.home.logo)
                .into(binding.layoutResult.imgTeam1)

            Glide.with(this)
                .load(it.away.logo)
                .into(binding.layoutResult.imgTeam2)
        })

        viewModel.getLeagueTitleLiveData().observe(this, Observer { title ->
            if (binding.tvTitle.text.isNullOrEmpty()) {
                binding.tvTitle.text = title
            }
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

    companion object {

        fun getIntent(context: Context, id: String, name: String): Intent {
            val intent = Intent(context, LeagueDetailActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            return intent
        }
    }
}