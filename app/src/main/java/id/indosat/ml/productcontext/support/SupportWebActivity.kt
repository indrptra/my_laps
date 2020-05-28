package id.indosat.ml.productcontext.support

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.util.MLLog
import id.indosat.ml.viewmodel.MLGeneralViewModel
import kotlinx.android.synthetic.main.activity_support_web.*
import java.net.URLEncoder

class SupportWebActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_web)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SUPPORT"
        supportActionBar?.elevation = 0f
        setupWebView()
        setChatClickListener()
        generalViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
        getURLSupport()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        support_webview?.let {
            if (keyCode == KeyEvent.KEYCODE_BACK && it.canGoBack()) {
                it.goBack()
                return true
            }
            return super.onKeyDown(keyCode, event)
        }?:run{
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun setChatClickListener(){
        support_chat_button?.setOnClickListener {

            val packageManager = this.packageManager
            val i = Intent(Intent.ACTION_VIEW)

            try {
                val url =
                    "https://api.whatsapp.com/send?phone=6285775405305&text=" + URLEncoder.encode("Hi Admin MyLearning! Saya ingin bertanya mengenai ", "UTF-8")
                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    this.startActivity(i)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            val visitorInfo = VisitorInfo.Builder()
//                .name(MLPrefModel.userFullName)
//                .email(MLPrefModel.userEmail)
//                .phoneNumber(MLPrefModel.userIdEmployee).build()
//            ZopimChat.setVisitorInfo(visitorInfo)
//            startActivity(Intent(this@SupportWebActivity,ZopimChatActivity::class.java))
            //showToast("SHOULD SHOW CHAT INTERFACE...")
        }
    }

    private fun setupWebView(){
        support_webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
        support_webview?.settings?.javaScriptEnabled = true
        support_webview?.setInitialScale(1)
        support_webview?.settings?.loadWithOverviewMode = true
        support_webview?.settings?.useWideViewPort = true
        support_webview?.webViewClient = SupportWebViewClient{isLoadFinished->
            if(isLoadFinished){
                support_web_load_progress?.visibility = View.GONE
                MLLog.showLog(classTag,"LOAD URL FINISHED")
            }else{
                support_web_load_progress?.visibility = View.VISIBLE
                MLLog.showLog(classTag,"START LOADING URL...")
            }
        }
    }

    private fun getURLSupport(){
        support_load_progress?.visibility = View.VISIBLE
        support_webview?.visibility = View.GONE
        support_web_load_progress?.visibility = View.GONE

        support_load_progress?.visibility = View.GONE
        support_webview?.visibility = View.VISIBLE
        support_web_load_progress?.visibility = View.VISIBLE

        support_webview?.loadUrl(MLConfig.BaseUrl + "v1/webview/staticpage?page=faq&token=${MLPrefModel.userToken}")

//      generalViewModel.getURLSupport{isValidToken,errorState,message,response->
//            support_load_progress?.visibility = View.GONE
//            support_webview?.visibility = View.VISIBLE
//            support_web_load_progress?.visibility = View.VISIBLE
//            if(!isValidToken){
//                navigateTo<LoginActivity>(true)
//            }else{
//                if(!errorState){
//                    support_webview?.loadUrl(response?.url ?: "")
//                }else{
//                    showToast(message)
//                }
//            }
//      }
    }

    private class SupportWebViewClient(private val callback:(Boolean)->Unit):WebViewClient(){

        override fun onPageFinished(view: WebView?, url: String?) {
            callback(true)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            callback(false)
        }
    }
}
