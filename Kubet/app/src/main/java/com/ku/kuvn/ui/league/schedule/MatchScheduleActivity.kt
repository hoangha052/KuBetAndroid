package com.ku.kuvn.ui.league.schedule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.ku.kuvn.databinding.ActivityMatchScheduleBinding

class MatchScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatchScheduleBinding
    private lateinit var viewModel: MatchScheduleViewModel
    private lateinit var adapter: MatchScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatchScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.defaultTopBar.tvTitle.text = intent.getStringExtra("name")
        binding.defaultTopBar.icBack.setOnClickListener {
            finish()
        }

        adapter = MatchScheduleAdapter()

        binding.rcSchedule.setHasFixedSize(true)
        binding.rcSchedule.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcSchedule.adapter = adapter

        val id = intent.getStringExtra("id")

        viewModel = ViewModelProvider(this, MatchScheduleViewModel.Factory(id)).get()
        viewModel.getMatchesLiveData().observe(this, Observer {
            adapter.addData(it)
        })
        viewModel.init()
    }

    companion object {

        fun getIntent(context: Context, id: String, name: String): Intent {
            val intent = Intent(context, MatchScheduleActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("name", name)
            return intent
        }
    }
}