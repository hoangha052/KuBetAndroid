package com.ku.kuvn.ui.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ku.kuvn.api.Game
import com.ku.kuvn.databinding.LayoutGameItemBinding

class GamePagerAdapter : RecyclerView.Adapter<GamePagerAdapter.ViewHolder>() {

    private val data = arrayListOf<Game>()
    var clickHandler: ((game: Game?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutGameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(games: List<Game>) {
        data.addAll(games)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(position)

    inner class ViewHolder(val binding: LayoutGameItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickHandler?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(game: Game) {
            Glide.with(binding.root.context)
                .load(game.thumb)
                .optionalCenterInside()
                .into(binding.imgGame)
        }
    }
}