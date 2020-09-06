package com.ku.kuvn.ui.league.standings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityStandingsBinding
import com.ku.kuvn.utils.MetricsUtil

class StandingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStandingsBinding
    private lateinit var viewModel: StandingsViewModel
    private lateinit var adapter: StandingsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStandingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.tvTitle.text = intent.getStringExtra("name")
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        adapter = StandingsAdapter()

        binding.rcStandings.setHasFixedSize(true)
        binding.rcStandings.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcStandings.addItemDecoration(StandingItemDecoration(
                MetricsUtil.convertDpToPixel(24f, this).toInt(),
                ContextCompat.getDrawable(this, R.drawable.bg_divider)))
        binding.rcStandings.adapter = adapter

        val id = intent.getStringExtra("id")

        viewModel = ViewModelProvider(this, StandingsViewModel.Factory(id)).get()
        viewModel.getStandingTeamsLiveData().observe(this, Observer {
            adapter.addData(it)
        })
        viewModel.init()
    }

    companion object {

        fun getIntent(context: Context, id: String, name: String): Intent {
            val intent = Intent(context, StandingsActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            return intent
        }
    }
}