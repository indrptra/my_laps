package id.indosat.ml.productcontext.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import id.indosat.ml.R
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.fragment_bottom_sheet_comment.*
import android.view.inputmethod.InputMethodManager
import android.app.Activity
import id.indosat.ml.util.showToast

open class FragmentBottomSheetComment:BottomSheetDialogFragment(){
    var currentAvatar=""
    var callbackComment :((String)->Unit)? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_comment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {act->
            if(!act.isFinishing){
                floating_comment_image?.loadURL(currentAvatar)
                floating_comment_text?.requestFocus()
                floating_comment_text?.setOnFocusChangeListener { view, b ->
                    if(!b){
                        hideKeyboard(view)
                    }
                }
                val imm = act.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                floating_comment_send?.setOnClickListener {
                    floating_comment_text?.text?.toString()?.let {comment->
                        if(comment.isEmpty()){
                            act.showToast("Cannot send empty comment...")
                        }else{
                            callbackComment?.invoke(comment.trim())
                        }
                    }
                }
            }
        }
    }

    fun hideKeyboard(view: View) {
        activity?.let {act->
            val imm = act.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)

        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            FragmentBottomSheetComment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}