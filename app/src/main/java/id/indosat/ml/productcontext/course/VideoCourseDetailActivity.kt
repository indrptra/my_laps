package id.indosat.ml.productcontext.course

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.pierfrancescosoffritti.androidyoutubeplayer.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.player.listeners.AbstractYouTubePlayerListener
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.util.FullScreenManager
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLCourseViewModel
import kotlinx.android.synthetic.main.activity_video_course_detail.*

class VideoCourseDetailActivity : BaseActivity() {
    private var fullScreenManager: FullScreenManager? = null
    private var isStatePause = false
    lateinit var courseViewModel: MLCourseViewModel


    companion object {
        const val videoURLName = "video-url-name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        fullScreenManager = FullScreenManager(this)
        setContentView(R.layout.activity_video_course_detail)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        //fullScreenManager?.enterFullScreen()
        supportActionBar?.hide()
        courseViewModel = ViewModelProviders.of(this, viewModelFactory).get(MLCourseViewModel::class.java)
        //youtube_player_view?.enterFullScreen()
        youtube_player_view?.playerUIController?.setFullScreenButtonClickListener {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            fullScreenManager?.exitFullScreen()
            supportActionBar?.show()
            finish()
        }
        youtube_player_view?.playerUIController?.showYouTubeButton(false)
        setInitVideo()
    }


    override fun onPause() {
        super.onPause()
        isStatePause = true
    }

    override fun onResume() {
        super.onResume()
        isStatePause = false
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        fullScreenManager?.enterFullScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        youtube_player_view?.release()
        courseViewModel.clear()
        try {
            generalViewModel.clear()
        } catch (e: Exception) {
            MLLog.showLog(classTag, e.localizedMessage)
        }
    }

    private fun setInitVideo() {
        youtube_player_view?.initialize({ it ->
            it.addListener(object : AbstractYouTubePlayerListener() {
                override fun onReady() {
                    intent.getStringExtra(videoURLName)?.let { vun ->
                        val arr = vun.split("/|//".toRegex())
                        if (arr.isNotEmpty()) {
//                            val lastUriStrings = arr.last().split("=".toRegex())
//                            youtube_player_view?.enterFullScreen()
//                            if(lastUriStrings.isNotEmpty()){
//                                it.loadVideo(lastUriStrings.last().trim(),0f)
//                            }else{
//                                it.loadVideo(arr.last().trim(),0f)
//                            }
                            val vidId = (arr.last().replace("watch?v=", "", true)).let {
                                val idx = it.indexOf("&", 0, true)
                                if (idx > 0) {
                                    it.substring(0, idx)
                                } else
                                    it
                            }
                            if (vidId.isNotEmpty()) {
                                youtube_player_view?.enterFullScreen()
                                it.loadVideo(vidId, 0f)
                            } else {
                                showToast("Invalid video Id")
                                finish()
                            }
                        } else {
                            showToast("Unable to play video")
                            finish()
                        }

                    } ?: run {
                        showToast("Unable to play video")
                        finish()
                    }

                }

                override fun onStateChange(state: PlayerConstants.PlayerState) {
                    if (state == PlayerConstants.PlayerState.ENDED) {
                        val courserId = intent.getIntExtra("courseId",0)
                        val moduleId = intent.getIntExtra("moduleId", 0)

                        courseViewModel.setPoint(courserId, moduleId,"\\mod_url\\event\\course_module_viewed") {
                                errorState, message, point ->

                            if (errorState) {
                                showToast(message)
                            }
                        }
                    }
                    if (isStatePause) {
                        it.pause()
                    }
                }
            })
        }, true)
    }
}
