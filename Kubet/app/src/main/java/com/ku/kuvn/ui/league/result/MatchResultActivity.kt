package com.ku.kuvn.ui.league.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.ku.kuvn.databinding.ActivityLeagueResultBinding

class MatchResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeagueResultBinding
    private lateinit var viewModel: MatchResultViewModel
    private lateinit var adapter: MatchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeagueResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.tvTitle.text = intent.getStringExtra("name")
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        adapter = MatchResultAdapter()

        binding.rcResult.setHasFixedSize(true)
        binding.rcResult.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcResult.adapter = adapter

        val id = intent.getStringExtra("id")

        viewModel = ViewModelProvider(this, MatchResultViewModel.Factory(id)).get()
        viewModel.getMatchesLiveData().observe(this, Observer {
            adapter.addData(it)
        })
        viewModel.init()
    }

    companion object {

        fun getIntent(context: Context, id: String, name: String): Intent {
            val intent = Intent(context, MatchResultActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            return intent
        }
    }
}