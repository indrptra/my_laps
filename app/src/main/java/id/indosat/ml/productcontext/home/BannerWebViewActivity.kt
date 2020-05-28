package id.indosat.ml.productcontext.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.util.MLLog
import kotlinx.android.synthetic.main.activity_banner_web_view.*

class BannerWebViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_banner_web_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "BANNER"
        supportActionBar?.elevation = 0f

        intent.getStringExtra("NAME").let {
            supportActionBar?.title = it
        }

        setupWebView()

        intent.getStringExtra("URL").let {
            setupView(it)
        }
    }


    private fun setupWebView(){
        banner_webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
        banner_webview?.settings?.javaScriptEnabled = true
        banner_webview?.setInitialScale(1)
        banner_webview?.settings?.loadWithOverviewMode = true
        banner_webview?.settings?.useWideViewPort = true
        banner_webview?.webViewClient = SupportWebViewClient { isLoadFinished ->
            if (isLoadFinished) {
                banner_web_load_progress?.visibility = View.GONE
                MLLog.showLog(classTag, "LOAD URL FINISHED")
            } else {
                banner_web_load_progress?.visibility = View.VISIBLE
                MLLog.showLog(classTag, "START LOADING URL...")
            }
        }
    }

    private fun setupView(url: String?){
        banner_load_progress?.visibility = View.VISIBLE
        banner_webview?.visibility = View.GONE
        banner_web_load_progress?.visibility = View.GONE

        banner_load_progress?.visibility = View.GONE
        banner_webview?.visibility = View.VISIBLE
        banner_web_load_progress?.visibility = View.VISIBLE

        banner_webview?.loadUrl(url)
    }


    private class SupportWebViewClient(private val callback:(Boolean)->Unit): WebViewClient(){

        override fun onPageFinished(view: WebView?, url: String?) {
            callback(true)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            callback(false)
        }
    }
}
