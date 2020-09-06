package com.ku.kuvn.ui.home

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ku.kuvn.R
import com.ku.kuvn.api.Banner
import com.ku.kuvn.api.Menu
import com.ku.kuvn.databinding.LayoutHomeBannerItemBinding
import com.ku.kuvn.databinding.LayoutHomeGameItemBinding
import com.ku.kuvn.databinding.LayoutHomeMenuItemBinding
import com.ku.kuvn.ui.home.banner.BannersAdapter

class HomeAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), LifecycleObserver {

    private val data = arrayListOf<HomeItem>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var scrollToNextRunnable: Runnable? = null

    var bannerClickHandler: ((banner: Banner?) -> Unit)? = null
    var gameClickHandler: (() -> Unit)? = null
    var sportClickHandler: (() -> Unit)? = null
    var menuClickHandler: ((menu: Menu) -> Unit)? = null

    companion object {
        const val TYPE_BANNER = 0
        const val TYPE_GAME = 1
        const val TYPE_SPORT = 2
        const val TYPE_MENU = 3
        const val INTERVAL = 3000L
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is BannerItem -> TYPE_BANNER
            is GameItem -> TYPE_GAME
            is SportItem -> TYPE_SPORT
            is MenuItem -> TYPE_MENU
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BANNER -> BannerViewHolder(
                    LayoutHomeBannerItemBinding.inflate(layoutInflater, parent, false),
                    (data[0] as BannerItem).banners)
            TYPE_GAME -> GameHolder(
                    LayoutHomeGameItemBinding.inflate(layoutInflater, parent, false))
            TYPE_SPORT -> SportHolder(
                    LayoutHomeGameItemBinding.inflate(layoutInflater, parent, false))
            else -> MenuViewHolder(LayoutHomeMenuItemBinding.inflate(layoutInflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannerViewHolder -> holder.bind()
            is GameHolder -> holder.bind()
            is SportHolder -> holder.bind()
            is MenuViewHolder -> holder.bind(data[position] as MenuItem)
        }
    }

    fun addData(homeItems: List<HomeItem>) {
        data.clear()
        data.addAll(homeItems)
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = data.takeIf { position >= 0 && position < data.size }?.get(
            position)

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        scrollToNextRunnable?.let {
            mainHandler.removeCallbacks(it)
            mainHandler.postDelayed(it, INTERVAL)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        scrollToNextRunnable?.let { mainHandler.removeCallbacks(it) }
    }

    inner class BannerViewHolder(private val binding: LayoutHomeBannerItemBinding,
                                 private val banners: List<Banner>) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            scrollToNextRunnable = Runnable {
                binding.rcBanners.setCurrentItem((binding.rcBanners.currentItem + 1) % banners.size, true)
                mainHandler.postDelayed(scrollToNextRunnable!!, INTERVAL)
            }

            val bannersAdapter = BannersAdapter()
            bannersAdapter.addData(banners)
            binding.pageIndicator.count = banners.size
            binding.rcBanners.offscreenPageLimit = banners.size
            binding.rcBanners.adapter = bannersAdapter
            binding.rcBanners.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.pageIndicator.setSelected(position)
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        scrollToNextRunnable?.let { mainHandler.removeCallbacks(it) }
                    } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                        scrollToNextItem(2000L)
                    }
                }
            })
            bannersAdapter.clickHandler = bannerClickHandler
            scrollToNextItem()
        }

        private fun scrollToNextItem(delay: Long = 0L) {
            scrollToNextRunnable?.let {
                mainHandler.removeCallbacks(it)
                mainHandler.postDelayed(it, INTERVAL + delay)
            }
        }

        fun bind() {
        }
    }

    inner class GameHolder(private val binding: LayoutHomeGameItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                gameClickHandler?.invoke()
            }
        }

        fun bind() {
            binding.container.setBackgroundResource(R.drawable.bg_ku_game)
            binding.tvTitle.setText(R.string.game_title)
        }
    }

    inner class SportHolder(private val binding: LayoutHomeGameItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                sportClickHandler?.invoke()
            }
        }

        fun bind() {
            binding.container.setBackgroundResource(R.drawable.bg_ku_sport)
            binding.tvTitle.setText(R.string.sport_title)
        }
    }

    inner class MenuViewHolder(val binding: LayoutHomeMenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val item = getItem(adapterPosition)
                if (item is MenuItem) {
                    menuClickHandler?.invoke(item.menu)
                }
            }
        }

        fun bind(menuItem: MenuItem) {
            Glide.with(binding.root.context).load(menuItem.menu.thumb)
                .placeholder(R.drawable.bg_gradient_banner)
                .optionalCenterInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imgMenu)
        }
    }
}