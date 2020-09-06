package com.ku.kuvn.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.ku.kuvn.databinding.ActivityInappBrowserBinding

class InAppBrowserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInappBrowserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInappBrowserBinding.inflate(layoutInflater)
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

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?)
                        = handleFallbackUrl(request?.url?.toString())

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?) = handleFallbackUrl(url)
            }
            binding.webView.settings.setAppCacheEnabled(true)
            binding.webView.settings.cacheMode = WebSettings.LOAD_DEFAULT
            binding.webView.loadUrl(url)
        }

        binding.imgClose.setOnClickListener {
            finish()
        }
    }

    private fun handleFallbackUrl(url: String?): Boolean {
        if (url != null && url.startsWith("intent://")) {
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            if (intent != null) {
                val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                if (fallbackUrl != null) {
                    binding.webView.loadUrl(fallbackUrl)
                    return true
                }
            }
        }
        return false
    }

    companion object {

        fun getIntent(context: Context, link: String): Intent {
            val intent = Intent(context, InAppBrowserActivity::class.java)
            intent.putExtra("link", link)
            return intent
        }
    }
}