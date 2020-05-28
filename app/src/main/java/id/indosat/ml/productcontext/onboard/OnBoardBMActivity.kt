package id.indosat.ml.productcontext.onboard

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.judemanutd.autostarter.AutoStartPermissionHelper
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.model.response.ResultOnBoarding
import id.indosat.ml.core.di.MLViewModelFactory
import id.indosat.ml.productcontext.home.HomeActivity
import id.indosat.ml.viewmodel.MLGeneralViewModel
import javax.inject.Inject


class OnBoardBMActivity : AppIntro() {
    @Inject
    lateinit var viewModelFactory: MLViewModelFactory

    lateinit var generalViewModel:MLGeneralViewModel

    companion object {
        const val resOnBoardingName = "response-onboarding-name"
        const val bodyMovinType = "bodymovin"
        const val imageType = "image"
        const val fileTypeBMRaw = "rawjson"
        const val fileTypeBMCompressed = "compressed"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        //window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setSeparatorColor(ContextCompat.getColor(this,android.R.color.transparent))
        setBarColor(ContextCompat.getColor(this,android.R.color.transparent))
        setNextArrowColor(ContextCompat.getColor(this,R.color.colorAccent))
        setColorDoneText(ContextCompat.getColor(this,R.color.colorAccent))
        setIndicatorColor(ContextCompat.getColor(this,R.color.colorAccent),
            ContextCompat.getColor(this,android.R.color.darker_gray))
        setColorSkipButton(ContextCompat.getColor(this,android.R.color.black))
        skipButtonEnabled = false
        intent.getParcelableExtra<ResultOnBoarding>(resOnBoardingName)?.let {rhs->
            rhs.rows.forEach {item->
                if(item.type == bodyMovinType){
                    addSlide(FragmentOBBM.newInstance(item.url))
                }else{
                    addSlide(FragmentOBImage.newInstance(item.url))
                }

            }
        }

        AutoStartPermissionHelper.getInstance().getAutoStartPermission(this@OnBoardBMActivity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = this.packageName
            val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent()
                intent.action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                intent.flags = FLAG_ACTIVITY_NEW_TASK
                intent.data = Uri.parse("package:$packageName")
                this.startActivity(intent)
            }

        }
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        navigateTo<HomeActivity>(true)
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        navigateTo<HomeActivity>(true)
    }

    override fun onSlideChanged(oldFragment: Fragment?, newFragment: Fragment?) {
        super.onSlideChanged(oldFragment, newFragment)
        newFragment?.let {
            if(it is FragmentOBBM){
                it.playLottie()
            }
        }
    }


}
