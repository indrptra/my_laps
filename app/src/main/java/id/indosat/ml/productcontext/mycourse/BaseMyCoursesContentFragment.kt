package id.indosat.ml.productcontext.mycourse


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemSortedCourse
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.base.BaseFragment
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.auth.UserViewModel
import kotlinx.android.synthetic.main.fragment_my_courses_content.*
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.dpToPx
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.showToast
import id.indosat.ml.common.ml_enum.MLEMyCourse

/**
 * A simple [Fragment] subclass.
 * Use the [BaseMyCoursesContentFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
open class BaseMyCoursesContentFragment : BaseFragment() {

    internal var contentTabString = ""
    private lateinit var userViewModel: UserViewModel
    private var resultAdapter = GroupAdapter<ViewHolder>()
    private var isLastPage = false
    private var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contentTabString = it.getString(BaseMyCoursesContentFragment.myCoursesTabName) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_courses_content, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        userViewModel.clear()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel = ViewModelProviders.of(this, viewModeFactory).get(UserViewModel::class.java)
        userViewModel.fromRowSorted = 0
        userViewModel.totalDataSorted = 0
        userViewModel.limitRowSorted = MLConfig.LimitData
        activity?.let {
            val glManager = GridLayoutManager(it, 2, RecyclerView.VERTICAL, false)
            mycourse_content_rv?.layoutManager = glManager
            mycourse_content_rv?.adapter = resultAdapter
            mycourse_content_rv?.addOnScrollListener(object : PaginationGLScrollListener(glManager) {
                override fun loadMoreItems() {
                    if (userViewModel.totalDataSorted <= userViewModel.fromRowSorted) {
                        return
                    }
                    getMyCourses(userViewModel.fromRowSorted)
                }

                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

            })
            getMyCourses()
        }

    }


    private fun getMyCourses(fromRow: Int = 0) {
        if (contentTabString.isNotEmpty()) {
            mycourse_content_note_container?.visibility = View.GONE
            mycourse_content_rv?.visibility = View.INVISIBLE
            if (fromRow > 0) {
                mycourse_content_progress?.visibility = View.GONE
                mycourse_content_progress_bottom?.visibility = View.VISIBLE
                mycourse_content_progress_bottom?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                mycourse_content_progress_bottom?.requestLayout()
                mycourse_content_rv?.requestLayout()
            } else {
                resultAdapter.clear()
                mycourse_content_progress?.visibility = View.VISIBLE
                mycourse_content_progress_bottom?.visibility = View.INVISIBLE
                mycourse_content_progress_bottom?.layoutParams?.height = 1
                mycourse_content_progress_bottom?.requestLayout()
                mycourse_content_rv?.requestLayout()
            }

            if (contentTabString.equals("completed")) {

                userViewModel.getCoursesByType(
                    contentTabString,
                    fromRow
                ) { isTokenValid, errorState, isThisLast, message, response ->
                    if (fromRow > 0) {
                        mycourse_content_progress_bottom?.layoutParams?.height = 1
                        mycourse_content_progress_bottom?.visibility = View.INVISIBLE
                        mycourse_content_progress_bottom?.requestLayout()
                        mycourse_content_rv?.requestLayout()
                    } else {
                        mycourse_content_progress?.visibility = View.GONE
                    }
                    isLoading = false
                    isLastPage = isThisLast
                    if (!isTokenValid) {
                        activity?.let {
                            it.navigateTo<LoginActivity>(true)
                        } ?: run {
                            mycourse_content_note_container?.visibility = View.VISIBLE
                            mycourse_content_note_text?.text = "INVALID TOKEN"
                            mycourse_content_rv?.visibility = View.GONE
                        }
                    } else {
                        if (!errorState) {
                            response?.let {
                                MLLog.showLog(classTag, it.toString())
                                if (it.result!!.isNotEmpty()) {
                                    activity?.let { act ->
                                        val eachWidthCard = (act.getDisplayMetrics().widthPixels - act.dpToPx(32)) / 2
                                        resultAdapter.addAll(it.result!!.map { row ->
                                            ItemSortedCourse(
                                                row,
                                                eachWidthCard,
                                                act.dpToPx(MLConfig.CardHeight),
                                                contentTabString == MLEMyCourse.RUNNING.type
                                            ) { view, innerRow ->
                                                act.navigateToCourseDetail(innerRow.id)
                                            }
                                        })
                                    }
                                }
                                mycourse_content_note_container?.visibility = View.GONE
                                mycourse_content_rv?.visibility = View.VISIBLE
                            } ?: run {
                                mycourse_content_note_container?.visibility = View.VISIBLE
                                mycourse_content_note_text?.text = message.toUpperCase()
                                mycourse_content_rv?.visibility = View.GONE
                            }
                        } else {
                            mycourse_content_note_container?.visibility = View.VISIBLE
                            mycourse_content_note_text?.text = message.toUpperCase()
                            mycourse_content_rv?.visibility = View.GONE
                        }

                    }
                }
            } else {
                userViewModel.getMyCourses(
                    contentTabString,
                    fromRow
                ) { isTokenValid, errorState, isThisLast, message, response ->
                    if (fromRow > 0) {
                        mycourse_content_progress_bottom?.layoutParams?.height = 1
                        mycourse_content_progress_bottom?.visibility = View.INVISIBLE
                        mycourse_content_progress_bottom?.requestLayout()
                        mycourse_content_rv?.requestLayout()
                    } else {
                        mycourse_content_progress?.visibility = View.GONE
                    }
                    isLoading = false
                    isLastPage = isThisLast
                    if (!isTokenValid) {
                        activity?.let {
                            it.navigateTo<LoginActivity>(true)
                        } ?: run {
                            mycourse_content_note_container?.visibility = View.VISIBLE
                            mycourse_content_note_text?.text = "INVALID TOKEN"
                            mycourse_content_rv?.visibility = View.GONE
                        }
                    } else {
                        if (!errorState) {
                            response?.let {
                                MLLog.showLog(classTag, it.toString())
                                if (it.rows.isNotEmpty()) {
                                    activity?.let { act ->
                                        val eachWidthCard = (act.getDisplayMetrics().widthPixels - act.dpToPx(32)) / 2
                                        resultAdapter.addAll(it.rows.map { row ->
                                            ItemSortedCourse(
                                                row,
                                                eachWidthCard,
                                                act.dpToPx(MLConfig.CardHeight),
                                                contentTabString == MLEMyCourse.RUNNING.type
                                            ) { view, innerRow ->
                                                act.navigateToCourseDetail(innerRow.id)
                                            }
                                        })
                                    }

                                }
                                mycourse_content_note_container?.visibility = View.GONE
                                mycourse_content_rv?.visibility = View.VISIBLE
                            } ?: run {
                                mycourse_content_note_container?.visibility = View.VISIBLE
                                mycourse_content_note_text?.text = message.toUpperCase()
                                mycourse_content_rv?.visibility = View.GONE
                            }
                        } else {
                            mycourse_content_note_container?.visibility = View.VISIBLE
                            mycourse_content_note_text?.text = message.toUpperCase()
                            mycourse_content_rv?.visibility = View.GONE
                        }

                    }
                }
            }
        } else {
            showToast("TAB SELECTED ERROR")
        }
    }

    companion object {
        const val myCoursesTabName = "my-courses-tab-name"
        @JvmStatic
        fun newInstance(param1: String) =
            BaseMyCoursesContentFragment().apply {
                arguments = Bundle().apply {
                    MLLog.showLog(classTag, param1)
                    putString(BaseMyCoursesContentFragment.myCoursesTabName, param1)
                }
            }
    }
}
