package id.indosat.ml.productcontext.onboard


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder

import id.indosat.ml.R
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.fragment_fragment_obbm.*
import kotlinx.android.synthetic.main.fragment_fragment_obimage.*


/**
 * A simple [Fragment] subclass.
 * Use the [FragmentOBImage.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentOBImage : Fragment(),ISlideBackgroundColorHolder{
    var imageUrl = ""

    override fun getDefaultBackgroundColor(): Int {
        return Color.parseColor("#FFCD00")
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        layoutContainerImage?.let{
            it.setBackgroundColor(backgroundColor)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(FragmentOBImage.imageUrlName)?.let {imgUrl->
                imageUrl = imgUrl
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_obimage, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {act->
            image_on_boarding?.loadURL(imageUrl,false)
        }
    }

    companion object {
        const val imageUrlName = "image-url-name"
        @JvmStatic
        fun newInstance(imageUrl:String) =
            FragmentOBImage().apply {
                arguments = Bundle().apply {
                    putString(FragmentOBImage.imageUrlName,imageUrl)
                }
            }
    }
}
