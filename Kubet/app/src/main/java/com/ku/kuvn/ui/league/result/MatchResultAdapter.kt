package com.ku.kuvn.ui.league.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.api.league.Match
import com.ku.kuvn.databinding.LayoutResultItemBinding
import com.ku.kuvn.utils.date.getDateWithServerTimeStamp
import java.text.SimpleDateFormat
import java.util.*

class MatchResultAdapter : RecyclerView.Adapter<MatchResultAdapter.ViewHolder>() {

    private val data = arrayListOf<Match>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy '-' hh:mm aa", Locale.getDefault())

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(matches: List<Match>) {
        data.addAll(matches)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: LayoutResultItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match) {
            val date = match.time.getDateWithServerTimeStamp()
            if (date != null) {
                binding.tvDate.text = dateFormat.format(date)
            }
            binding.tvTeamName1.text = match.home.name
            binding.tvTeamName2.text = match.away.name
            binding.tvScore.text = "%d - %d".format(Locale.US, match.homeScore, match.awayScore)

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