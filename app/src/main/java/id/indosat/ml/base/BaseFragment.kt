package id.indosat.ml.base


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import dagger.android.support.AndroidSupportInjection
import id.indosat.ml.R
import javax.inject.Inject
import id.indosat.ml.core.di.MLViewModelFactory
abstract class BaseFragment : Fragment() {
    internal var classTag = ""
    @Inject
    lateinit var viewModeFactory:MLViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return TextView(activity).apply {
            setText(R.string.hello_blank_fragment)
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }

}
