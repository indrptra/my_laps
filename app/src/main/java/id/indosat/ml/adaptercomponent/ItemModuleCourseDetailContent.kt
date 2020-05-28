package id.indosat.ml.adaptercomponent

import android.view.View
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.common.model.response.ModuleCourseDetail
import id.indosat.ml.productcontext.course.CourseDetailActivity
import id.indosat.ml.util.loadURL
import kotlinx.android.synthetic.main.item_module_course_detail_content.*

open class ItemModuleCourseDetailContent(
    private val module: ModuleCourseDetail,
    val pos: Int = 0,
    private val total: Int = 0,
    private val callback: (ModuleCourseDetail, Int, Boolean) -> Unit
) : Item() {
    override fun getLayout(): Int {
        return R.layout.item_module_course_detail_content
    }


    private var isModuleComplete = false
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.item_module_content_title?.text = module.name
        viewHolder.item_module_content_icon?.context?.let { ctx ->
            if (ctx is CourseDetailActivity) {
                if (!ctx.isFinishing) {
                    viewHolder.item_module_content_icon?.loadURL(module.thumbnail)
//                    viewHolder.item_module_content_icon?.loadURL(
//                        when (module.type) {
//                            "url" -> "https://mylearning.indosatooredoo.com/theme/image.php/mylearning/url/1557212691/icon"
//                            else -> "https://mylearning.indosatooredoo.com/theme/image.php/mylearning/core/1556815315/f/pdf"
//                        }
//                    )
                    module.usercompletion?.let { uc ->
                        if (isModuleComplete) {
                            viewHolder.item_module_completion_icon?.visibility = View.VISIBLE
                        } else {
                            if (uc.completionstate) {
                                viewHolder.item_module_completion_icon?.visibility = View.VISIBLE
                            } else {
                                viewHolder.item_module_completion_icon?.visibility = View.GONE
                            }
                        }


                    }
                }
            }
        }

        viewHolder.item_module_content_container?.setOnClickListener {
            callback(module, pos + 1, isModuleComplete)
        }

        viewHolder.item_module_content_container?.context?.let { ctx ->
            if (total == 1) {
                viewHolder.item_module_content_container?.background =
                    ContextCompat.getDrawable(ctx, R.drawable.border_gray_bg_white_radius_bottom_single)
            } else if (pos == total) {
                viewHolder.item_module_content_container?.background =
                    ContextCompat.getDrawable(ctx, R.drawable.border_gray_bg_white_radius_bottom)
            } else if (pos == (total - 1)) {
                viewHolder.item_module_content_container?.background =
                    ContextCompat.getDrawable(ctx, R.drawable.border_gray_bg_white_box)
            } else {
                viewHolder.item_module_content_container?.background =
                    ContextCompat.getDrawable(ctx, R.drawable.border_gray_bg_white_box_bottomless)

            }
        }

    }

    fun setModuleComplete() {
        isModuleComplete = true
    }
}