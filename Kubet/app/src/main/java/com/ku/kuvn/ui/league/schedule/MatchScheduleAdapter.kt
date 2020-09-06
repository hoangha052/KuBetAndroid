package com.ku.kuvn.ui.league.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.api.league.Match
import com.ku.kuvn.databinding.LayoutMatchItemBinding
import com.ku.kuvn.utils.date.getDateWithServerTimeStamp
import java.text.SimpleDateFormat
import java.util.*

class MatchScheduleAdapter : RecyclerView.Adapter<MatchScheduleAdapter.ViewHolder>() {

    private val data = arrayListOf<Match>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy '-' hh:mm aa", Locale.getDefault())

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutMatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(matches: List<Match>) {
        data.addAll(matches)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutMatchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            val date = match.time.getDateWithServerTimeStamp()
            if (date != null) {
                binding.tvDate.text = dateFormat.format(date)
            }
            binding.tvTeamName1.text = match.home.name
            binding.tvTeamName2.text = match.away.name

            Glide.with(binding.root.context)
                .load(match.home.logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgTeam1)

            Glide.with(binding.root.context)
                .load(match.away.logo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgTeam2)
        }
    }
}