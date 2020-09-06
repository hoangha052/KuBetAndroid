package com.ku.kuvn.ui.gift

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.R
import com.ku.kuvn.api.Blog
import com.ku.kuvn.databinding.LayoutGiftItemBinding

class GiftAdapter : RecyclerView.Adapter<GiftAdapter.ViewHolder>() {

    private val data = arrayListOf<Blog>()
    var clickHandler: ((gift: Blog?) -> Unit)? = null

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutGiftItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(gifts: List<Blog>) {
        data.clear()
        data.addAll(gifts)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(position)

    inner class ViewHolder(val binding: LayoutGiftItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickHandler?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(blog: Blog) {
            binding.tvTitle.text = blog.title

            Glide.with(binding.root.context)
                .load(blog.thumb)
                .optionalCenterInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgGift)
        }
    }
}