package com.ku.kuvn.ui.home.banner

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.api.Banner
import com.ku.kuvn.databinding.LayoutBannerItemBinding

class BannersAdapter: RecyclerView.Adapter<BannersAdapter.ViewHolder>() {

    private val data = arrayListOf<Banner>()
    var clickHandler: ((banner: Banner?) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutBannerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun addData(banners: List<Banner>) {
        data.addAll(banners)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(position)

    inner class ViewHolder(val binding: LayoutBannerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                clickHandler?.invoke(getItem(adapterPosition))
            }
        }

        fun bind(banner: Banner) {
            Glide.with(binding.root.context).load(banner.thumb)
                .optionalCenterInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.root)
        }
    }
}