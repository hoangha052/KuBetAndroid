package com.ku.kuvn.ui.league.standings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.databinding.LayoutStandingTeamGroupItemBinding
import com.ku.kuvn.databinding.LayoutStandingTeamItemBinding

class StandingsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val data = arrayListOf<StandingItem>()

    companion object {
        const val TYPE_GROUP = 0
        const val TYPE_TEAM = 1
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is GroupItem -> TYPE_GROUP
            is TeamItem -> TYPE_TEAM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_GROUP -> GroupViewHolder(LayoutStandingTeamGroupItemBinding.inflate(layoutInflater, parent, false))
            else -> TeamViewHolder(LayoutStandingTeamItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GroupViewHolder -> holder.bind(data[position] as GroupItem)
            is TeamViewHolder -> holder.bind(data[position] as TeamItem)
        }
    }

    fun addData(standings: List<StandingItem>) {
        data.addAll(standings)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(position)

    inner class GroupViewHolder(val binding: LayoutStandingTeamGroupItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(group: GroupItem) {
            binding.tvTitle.text = group.title
        }
    }

    inner class TeamViewHolder(val binding: LayoutStandingTeamItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teamItem: TeamItem) {
            val team = teamItem.team
            binding.tvTeamName.text = team.name
            binding.tvWin.text = team.win.toString()
            binding.tvDraw.text = team.draw.toString()
            binding.tvLose.text = team.lose.toString()
            binding.tvPoints.text = team.points.toString()

            Glide.with(binding.root.context)
                .load(team.logo)
                .optionalCenterInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgTeam)
        }
    }
}