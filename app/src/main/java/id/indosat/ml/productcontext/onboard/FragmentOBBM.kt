package id.indosat.ml.productcontext.onboard


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieTask
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder

import id.indosat.ml.R
import id.indosat.ml.util.showToast
import kotlinx.android.synthetic.main.fragment_fragment_obbm.*


class FragmentOBBM : Fragment(),ISlideBackgroundColorHolder{
    var bmUrlPath = ""
    override fun getDefaultBackgroundColor(): Int {
        return Color.parseColor("#FFFFFF")
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        layoutContainer?.let{
            it.setBackgroundColor(backgroundColor)
        }
    }

    fun playLottie(){
        lottie_view?.playAnimation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(FragmentOBBM.bmUrlName)?.let {urlPath->
                bmUrlPath = urlPath
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_obbm, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {act->
            //lottie_view?.useHardwareAcceleration(true)
            lottie_view?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
            val lTask = LottieCompositionFactory.fromUrl(act,bmUrlPath)
            registerTask(lTask)
        }
    }


    private fun registerTask(task: LottieTask<LottieComposition>) {
        task.addListener {lComp->
                lComp?.let {
                    lottie_view?.setComposition(it)
                    //lottie_view?.playAnimation()
                }

            }
            .addFailureListener {
                showToast(it.localizedMessage ?: "")
            }
    }

    companion object {
        const val bmUrlName = "bm-url-name"
        @JvmStatic
        fun newInstance(urlString:String) =
            FragmentOBBM().apply {
                arguments = Bundle().apply {
                    putString(FragmentOBBM.bmUrlName,urlString)
                }
            }
    }
}
