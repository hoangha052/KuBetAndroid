package com.ku.kuvn.ui.game

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.ku.kuvn.databinding.ActivityGameDetailBinding

class GameDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url: String? = intent?.getStringExtra("link")
        if (!url.isNullOrEmpty()) {
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    binding.progressBar.visibility = View.VISIBLE
                    super.onPageStarted(view, url, favicon)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    binding.progressBar.visibility = View.GONE
                    super.onPageFinished(view, url)
                }
            }
            binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            binding.webView.loadUrl(url)
        }

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    companion object {

        fun getIntent(context: Context, link: String): Intent {
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("link", link)
            return intent
        }
    }
}