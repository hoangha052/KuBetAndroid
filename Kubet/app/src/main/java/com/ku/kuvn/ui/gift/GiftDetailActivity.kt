package com.ku.kuvn.ui.gift

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.ku.kuvn.R
import com.ku.kuvn.databinding.ActivityGiftDetailBinding

class GiftDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGiftDetailBinding
    private lateinit var viewModel: GiftDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGiftDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgClose.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(this).get()
        viewModel.getGiftsLiveData().observe(this, Observer { gift ->
            Glide.with(this).load(gift.thumb)
                .placeholder(R.drawable.bg_gradient_banner)
                .optionalCenterInside().into(binding.imgBanner)

            binding.tvTitle.text = gift.title
            binding.webViewDesc.loadData(gift.description, "text/html; charset=utf-8", "UTF-8")
            binding.webViewContent.loadData(gift.content, "text/html; charset=utf-8", "UTF-8")
        })
        viewModel.getShowLoadingLiveData().observe(this, Observer { active ->
            binding.progressBar.isVisible = active
        })

        val id: String? = intent?.getStringExtra("id")
        if (!id.isNullOrEmpty()) {
            binding.webViewDesc.webViewClient = WebViewClient()
            binding.webViewContent.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                    super.onPageFinished(view, url)
                }
            }
            binding.webViewDesc.settings.defaultTextEncodingName = "UTF-8"
            binding.webViewContent.settings.defaultTextEncodingName = "UTF-8"
            binding.webViewContent.settings.setAppCacheEnabled(true)
            binding.webViewContent.settings.cacheMode = WebSettings.LOAD_DEFAULT
            viewModel.init(id)
        }
    }

    companion object {

        fun getIntent(context: Context, id: String): Intent {
            val intent = Intent(context, GiftDetailActivity::class.java)
            intent.putExtra("id", id)
            return intent
        }
    }
}