package id.indosat.ml.util

import android.annotation.TargetApi
import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class SupportWebViewClient(private val callback:(Boolean)->Unit,
                           private val urlCallback:(String,Boolean)->Unit,
                           private val errorCallback:(Boolean)->Unit): WebViewClient(){

    override fun onPageFinished(view: WebView?, url: String?) {
        callback(true)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        callback(false)
    }

    @SuppressWarnings("deprecation")
    @Override
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return handleUri(view,url?:"")
    }


    @TargetApi(Build.VERSION_CODES.N)
    @Override
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return handleUri(view,request?.url?.toString() ?: "")
    }


    private fun handleUri(view: WebView?, url: String): Boolean {
        view?.context?.let {
            if(!DetectConnection.checkInternetConnection(it)){
                urlCallback(url,true)
            }else{
                view?.loadUrl(url)
            }
        }

        return true
    }


    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        error?.toString()
        errorCallback(true)
    }
}