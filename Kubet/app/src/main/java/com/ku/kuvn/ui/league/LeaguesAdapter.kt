package com.ku.kuvn.ui.league

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.api.league.League
import com.ku.kuvn.databinding.LayoutLeagueItemBinding

class LeaguesAdapter : RecyclerView.Adapter<LeaguesAdapter.ViewHolder>() {

    private val data = arrayListOf<League>()
    var clickHandler: ((league: League?) -> Unit)? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutLeagueItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(leagues: List<League>) {
        data.addAll(leagues)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(position)

    inner class ViewHolder(val binding: LayoutLeagueItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickHandler?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(league: League) {
            binding.tvTitle.text = league.name
            Glide.with(binding.root.context)
                .load(league.thumb)
                .optionalCenterInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgLeague)
        }
    }
}