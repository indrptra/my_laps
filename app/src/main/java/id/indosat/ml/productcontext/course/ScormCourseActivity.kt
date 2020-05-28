package id.indosat.ml.productcontext.course


import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.util.DetectConnection
import id.indosat.ml.util.FullScreenManager
import id.indosat.ml.util.SupportWebViewClient
import id.indosat.ml.util.showToast
import kotlinx.android.synthetic.main.activity_scorm_course.*

class ScormCourseActivity : BaseActivity() {
    companion object {
        const val scormUrlPathName = "scorm-url-path-name"
        const val scormCourseTitle = "scorm-course-title"
    }
    private var currentURL = ""
    private var fullScreenManager: FullScreenManager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_scorm_course)
        supportActionBar?.title = "My Learning Course"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fullScreenManager = FullScreenManager(this)
        intent.getStringExtra(scormUrlPathName)?.let {
            if(it.isEmpty()){
                finish()
                showToast("Invalid course, please try again")
            }else{
                currentURL = it
            }
        }
        intent.getStringExtra(scormCourseTitle)?.let {
            if(it.isNotEmpty()){
                supportActionBar?.title = it
            }
        }
        setupWebView()
        scorm_try_again_button?.setOnClickListener {
            checkAndLoad()
        }

        if(savedInstanceState == null){
            checkAndLoad()
        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        scorm_webview?.saveState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        scorm_webview?.restoreState(savedInstanceState)
    }

//    override fun onConfigurationChanged(newConfig: Configuration?) {
//        super.onConfigurationChanged(newConfig)
//        newConfig?.let {
//            if(it.orientation == Configuration.ORIENTATION_LANDSCAPE){
//                fullScreenManager?.enterFullScreen()
//            }else{
//                fullScreenManager?.exitFullScreen()
//            }
//        }
//    }

    private fun checkAndLoad(){
        if(!DetectConnection.checkInternetConnection(this)){
            scorm_webview?.visibility = View.GONE
            scorm_web_load_progress?.visibility = View.GONE
            scorm_error_connection_container?.visibility = View.VISIBLE
        }else {
            scorm_webview?.visibility = View.VISIBLE
            scorm_error_connection_container?.visibility = View.GONE
            loadURL()
        }
    }

    private fun loadURL(){
        scorm_webview?.loadUrl(currentURL)
    }


    override fun onResume() {
        super.onResume()
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        scorm_webview?.let {
            if (keyCode == KeyEvent.KEYCODE_BACK && it.canGoBack()) {
                it.goBack()
                return true
            }
            return super.onKeyDown(keyCode, event)
        }?:run{
            return super.onKeyDown(keyCode, event)
        }
    }

    private fun setupWebView(){
        val uri = Uri.parse(this.currentURL)
        val isSkillSoft  = uri.getQueryParameter("skillsoft")?.toInt() == 1

        scorm_webview?.setLayerType(View.LAYER_TYPE_HARDWARE,null)

        scorm_webview?.setInitialScale(1)
        val userAgent = scorm_webview?.settings?.userAgentString ?: ""
        val cleanUserAgent = userAgent.replace("Android","").replace("android","")
        //scorm_webview?.settings?.userAgentString = "Mozilla/5.0 (Linux) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/73.0.3683.75 Mobile Safari/537.36"
        scorm_webview?.settings?.userAgentString = if (isSkillSoft) userAgent else cleanUserAgent
        scorm_webview?.settings?.allowContentAccess = true
        scorm_webview?.settings?.domStorageEnabled = true
        scorm_webview?.settings?.allowFileAccessFromFileURLs = true
        scorm_webview?.settings?.allowUniversalAccessFromFileURLs = true
        //scorm_webview?.settings?.builtInZoomControls = true
        scorm_webview?.settings?.loadWithOverviewMode = true
        scorm_webview?.settings?.useWideViewPort = true
        scorm_webview?.settings?.javaScriptEnabled = true
        scorm_webview?.webViewClient = SupportWebViewClient(callback = { isLoadFinished->
            if(isLoadFinished){
                scorm_web_load_progress?.visibility = View.GONE
                Log.d("MAIN ACT","LOAD URL FINISHED")
            }else{
                scorm_web_load_progress?.visibility = View.VISIBLE
                Log.d("MAIN ACT","START LOADING URL...")
            }
        },urlCallback = {_urlCall,errorState->
            currentURL = _urlCall
            if(errorState){
                scorm_webview?.visibility = View.GONE
                scorm_web_load_progress?.visibility = View.GONE
                scorm_error_connection_container?.visibility = View.VISIBLE
            }
        },errorCallback = {isError->
            if(isError) {

            }else {

            }
        })

    }
}
