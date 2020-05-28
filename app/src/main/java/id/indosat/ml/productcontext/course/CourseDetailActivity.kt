package id.indosat.ml.productcontext.course

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannedString
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.eventbus.Subscribe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.*
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.getProgressDialog
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToScorm
import id.indosat.ml.common.coordinator.navigateToYoutube
import id.indosat.ml.common.ml_enum.MLECourseDetailModuleType
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ModuleCourseDetail
import id.indosat.ml.common.model.response.RowsCourseDetail
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.loadURL
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLCourseViewModel
import kotlinx.android.synthetic.main.activity_course_detail.*
import kotlinx.android.synthetic.main.content_course_detail.*
import kotlinx.android.synthetic.main.header_course_detail_rv.*
import de.greenrobot.event.EventBus
import id.indosat.ml.base.HaloEvent


class CourseDetailActivity : BaseActivity()  {
    lateinit var courseViewModel: MLCourseViewModel
    val bus: EventBus = EventBus.getDefault()


    companion object {
        const val courseID = "course-detail-id"
        const val KEY_DIRECT_START = "start_course"
    }

    private var progressDialog: AlertDialog? = null
    private var courseDetailAdapter = GroupAdapter<ViewHolder>()
    private var isLastPage = false
    private var isLoading = false
    private var courseTitle = ""
    private var courseId = 0
    private var modulePlacementPos = 0
    private var isModuleClicked = false
    private var isScorm = false
    override fun onDestroy() {
        super.onDestroy()
        courseViewModel.clear()
        try {
            generalViewModel.clear()
        } catch (e: Exception) {
            MLLog.showLog(classTag, e.localizedMessage)
        }
    }

    override fun onStop() {
        super.onStop()
        loaderCourseBackToNormal()
        isModuleClicked = false
    }

    override fun onPause() {
        super.onPause()
        bus.unregister(this)
    }


    override fun onResume() {
        super.onResume()
        bus.register(this)
        if (latestModulePosClicked < 0) {
            return
        }
        val itemModule = courseDetailAdapter.getItem(latestModulePosClicked)
        if (itemModule is ItemModuleCourseDetailContent) {
            itemModule.setModuleComplete()
            courseDetailAdapter.notifyItemChanged(latestModulePosClicked)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_course_detail)
        setSupportActionBar(toolbar_course_detail)
        toolbar_course_detail?.title = ""
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //supportActionBar?.setDisplayShowHomeEnabled(true)
        collapsing_toolbar?.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent))
        courseViewModel = ViewModelProviders.of(this, viewModelFactory).get(MLCourseViewModel::class.java)
        resetViewModelLimit()
        courseId = intent.getIntExtra(courseID, 0)
        setProgressDialog()
        setStartClickListener()
        //setDetailImageHeader("https://cdn3.movieweb.com/i/article/zaIo4DjiyUpjrKAcCUFpqJ7ALrq4Ow/798:50/Avengers-4-Rumor-Original-Team-Fate.jpg")
        setCommentList()
        startEnroll()
    }

    private var commentListVerticalOffset = 0

    private fun resetViewModelLimit() {
        courseViewModel.fromRowComment = 0
        courseViewModel.limitRowComment = MLConfig.LimitData
        courseViewModel.totalDataComment = 0
    }

    private fun setCommentList() {
        courseDetailAdapter.clear()
        val llm = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        content_course_comment_rv?.layoutManager = llm
        content_course_comment_rv?.adapter = courseDetailAdapter
        content_course_comment_rv?.addOnScrollListener(object : PaginationGLScrollListener(llm) {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                this.checkScrolled()
                commentListVerticalOffset += dy
                try {
                    val header = courseDetailAdapter.getItem(0)

                    if (commentListVerticalOffset == 0) {
                        //content_toolbar_title?.text = ""
                        //supportActionBar?.title = ""
                        //collapsing_toolbar?.isTitleEnabled = false
                    } else {
                        //content_toolbar_title?.text = courseTitle
                        //collapsing_toolbar?.isTitleEnabled = true
                    }

                    if (header is HeaderCourseDetail) {
                        header.contentStartButtonContainer?.top?.let { startTop ->
                            if (commentListVerticalOffset > startTop) {
                                content_course_sticky_header?.visibility = View.VISIBLE
                            } else {
                                content_course_sticky_header?.visibility = View.GONE
                            }
                        }
                    }
                } catch (e: Exception) {
                    MLLog.showLog(classTag, e.localizedMessage)
                }
            }

            override fun loadMoreItems() {
                if (courseViewModel.totalDataComment <= courseViewModel.fromRowComment) {
                    return
                }
                getComments(courseViewModel.fromRowComment, MLConfig.LimitData, courseId)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun setDetailImageHeader(url: String) {
        course_detail_header?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        val dblWidth = getDisplayMetrics().widthPixels.toDouble()
        val height = (dblWidth * (0.5625))
        course_detail_header?.layoutParams?.height = height.toInt()
        //getDisplayMetrics().heightPixels / 3
        if (!isFinishing) {
            course_detail_header?.loadURL(url)
        }
    }

    private var textLoader: TextView? = null
    private fun setProgressDialog() {
        val pairOf = getProgressDialog()
        progressDialog = pairOf.first
        textLoader = pairOf.second.findViewById(R.id.progress_dialog_loading_text)
    }

    private fun setStartClickListener() {
        content_course_start_button_sticky?.setOnClickListener {
            startCourse()
        }
    }

    private fun startCourse() {
        content_course_start_button_sticky?.visibility = View.GONE
        content_course_start_button_normal?.visibility = View.GONE
        content_course_start_button_progress?.visibility = View.VISIBLE
        content_course_start_button_normal_progress?.visibility = View.VISIBLE
        courseViewModel.getCourseScormURL(courseId) { validToken, errorState, message, url ->
            if (!validToken) {
                showToast(message)
                navigateTo<LoginActivity>(true)
            } else {
                if (errorState) {
                    showToast(message)
                } else {
                    url?.let {
                        if (it.isNotEmpty()) {
                            navigateToScorm(it, courseTitle)
                        } else {
                            loaderCourseBackToNormal()
                            showToast("Invalid course,try again later")
                        }
                    } ?: run {
                        loaderCourseBackToNormal()
                        showToast("Invalid course,try again later")
                    }
                }
            }
        }
    }

    private fun loaderCourseBackToNormal() {
        if (!isScorm) {
            content_course_start_button_sticky?.visibility = View.GONE
            content_course_start_button_normal?.visibility = View.GONE
        } else {
            content_course_start_button_sticky?.visibility = View.VISIBLE
            content_course_start_button_normal?.visibility = View.VISIBLE
        }
        content_course_start_button_progress?.visibility = View.GONE
        content_course_start_button_normal_progress?.visibility = View.GONE
    }

    private fun startEnroll() {
        progressDialog?.show()
        courseViewModel.startEnroll(courseId) { errorState, message, response ->
            progressDialog?.dismiss()
            if (!errorState) {
                getCourseDetail()
            } else {
                if (response == null) {
                    //for course already enrolled error
                    getCourseDetail()
                } else {
                    //for other error
                    showToast(message)
                }
            }
        }
    }

    private fun getCourseDetail() {
        progressDialog?.show()
        modulePlacementPos = 0
        courseViewModel.getCourseDetail(courseId) { isTokenValid, errorState, message, response ->
            progressDialog?.dismiss()

            if (!isTokenValid) {
                showToast(message)
                navigateTo<LoginActivity>(true)
            } else {
                if (!errorState) {
                    response?.rows?.let { row ->
                        courseDetailAdapter.clear()
                        courseTitle = row.fullname
                        //content_toolbar_title?.text = courseTitle
                        collapsing_toolbar?.title = courseTitle

                        courseDetailAdapter.add(0, HeaderCourseDetail(row, {
                            startCourse()
                        }, { rowRating ->
                            showRatingDialog(rowRating)
                        }))
                        setDetailImageHeader(row.imagefileurl)
                        isScorm = row.isScorm
                        if (!row.isScorm) {
                            content_course_start_button_sticky?.visibility = View.GONE
                            var isHeaderModuleHeaderAdded = false
                            if (row.modules.isNotEmpty()) {
                                for (m in row.modules) {
//                                    m.content
//                                    if (m.type.toLowerCase() == MLECourseDetailModuleType.URL.type.toLowerCase() ||
//                                        m.type.toLowerCase() == MLECourseDetailModuleType.RESOURCE.type.toLowerCase()
//                                    ) {
                                        //TO DO : add course detail module here
                                        if (!isHeaderModuleHeaderAdded) {
                                            // add header modules
                                            modulePlacementPos += 1
                                            courseDetailAdapter.add(HeaderCourseDetailModule())
                                            courseDetailAdapter.add(
                                                ItemModuleCourseDetailContent(
                                                    m,
                                                    modulePlacementPos,
                                                    row.modules.size
                                                ) { mcd, _pos, isComplete ->
                                                    moduleClicked(mcd, _pos)
                                                })
                                            isHeaderModuleHeaderAdded = true
                                        } else {
                                            modulePlacementPos += 1
                                            courseDetailAdapter.add(
                                                ItemModuleCourseDetailContent(
                                                    m,
                                                    modulePlacementPos,
                                                    row.modules.size
                                                ) { mcd, _pos, isComplete ->
                                                    moduleClicked(mcd, _pos)
                                                })

                                        }

                                    //}
                                }
                                /*if(modulePlacementPos == 1){
                                    modulePlacementPos = 0
                                }*/
                            } else {
                                //modulePlacementPos = 0 //if(modulePlacementPos <= 0) 0 else modulePlacementPos-1
                            }
                        }
                        getComments(0, MLConfig.LimitData, courseId)
                    } ?: run {
                        showToast(message)
                    }
                } else {
                    showToast(message)
                }
                if (intent?.getBooleanExtra(KEY_DIRECT_START, false) == true) {
                    startCourse()
                }
            }
        }
    }

    private fun disableModulesClick() {

    }


    private var latestModulePosClicked = -1
    private fun moduleClicked(module: ModuleCourseDetail, placementPos: Int) {
        if (isModuleClicked) {
            showToast("loading content please wait...")
            return
        }
        isModuleClicked = true
        latestModulePosClicked = placementPos
        textLoader?.text = "Loading content please wait..."
        progressDialog?.show()
        courseViewModel.setEnrollOrCompleteNew(courseId, module.id) { isValidToken, errorState, message, _ ->
            progressDialog?.dismiss()
//            if (!isValidToken) {
//                showToast(message)
//                navigateTo<LoginActivity>(true)
//            } else {
                if (!errorState || message.equals("This module is already completed")) {
                    module.content?.let { _content ->
                        if (_content.filetype.toLowerCase() == MLECourseDetailModuleType.VIDEO.type.toLowerCase()) {
                            //navigateTo<VideoCourseDetailActivity>()
                            //showToast(module.name)
                            navigateToYoutube(module.id, courseId, _content.fileurl)
                        } else {
                            val viewIntent = Intent(Intent.ACTION_VIEW)
                            when(_content.filetype.toLowerCase()) {
                                MLECourseDetailModuleType.PDF.type.toLowerCase() -> {
                                    viewIntent.setDataAndType(Uri.parse(_content.fileurl), "application/pdf")
                                    startActivity(viewIntent)
                                }
                                MLECourseDetailModuleType.SCORM.type.toLowerCase() -> navigateToScorm(_content.fileurl, courseTitle)
                                MLECourseDetailModuleType.CERTIFICATE.type.toLowerCase() -> navigateToScorm(_content.fileurl, courseTitle)
                                MLECourseDetailModuleType.QUIZ.type.toLowerCase() -> navigateToScorm(_content.fileurl, courseTitle)

//                              else -> //viewIntent.data = Uri.parse(_content.fileurl)
//                                    viewIntent.setDataAndType(Uri.parse(_content.fileurl), "application/*")
                            }

                            var action = ""
                            when(_content.filetype.toLowerCase()) {
                                MLECourseDetailModuleType.PDF.type.toLowerCase() -> action = "\\mod_resource\\event\\course_module_viewed"
                                MLECourseDetailModuleType.RESOURCE.type.toLowerCase() -> action = "\\mod_resource\\event\\course_module_viewed"
                                MLECourseDetailModuleType.SCORM.type.toLowerCase() -> action = "\\mod_scorm\\event\\course_module_viewed"
                                MLECourseDetailModuleType.URL.type.toLowerCase() -> action = "\\mod_url\\event\\course_module_viewed"
                                MLECourseDetailModuleType.VIDEO.type.toLowerCase() -> action = "\\mod_url\\event\\course_module_viewed"
                                MLECourseDetailModuleType.QUIZ.type.toLowerCase() -> action = "\\mod_quiz\\event\\course_module_viewed"
                            }

                            if (action.isNotEmpty()) {
                                courseViewModel.setPoint(courseId, module.id, action) {
                                        errorState, message, point ->

                                    if (errorState) {
                                        showToast(message)
                                    }
                                }
                            }

                        }
                    } ?: run {
                        showToast("Invalid course content")
                    }
                } else {
                    isModuleClicked = false
                    showToast(message)
                }
            }
//        }
    }

    private fun getComments(fromRow: Int = 0, limitRow: Int = MLConfig.LimitData, courseId: Int) {
        isLoading = true
        toggleProgress(fromRow, true)
        courseViewModel.getCourseDetailComments(
            fromRow,
            limitRow,
            courseId
        ) { isValidToken, errorState, isThisLast, message, response ->
            toggleProgress(fromRow, false)
            progressDialog?.dismiss()
            isLoading = false
            isLastPage = isThisLast
            if (!isValidToken) {
                showToast(message)
                navigateTo<LoginActivity>(true)
            } else {
                if (!errorState) {
                    response?.let { res ->
                        if (fromRow == 0) {
                            resetHeadersContent()
                            addHeaderComment(res.numfound)
                        }

                        if (res.rows.isNotEmpty()) {
                            courseDetailAdapter.addAll(res.rows.map { rowComment -> ItemComment(rowComment) })
                        }
                    } ?: run {
                        //showToast(message)
                        if (fromRow == 0) {
                            addHeaderComment(0)
                        }
                    }
                } else {
                    //showToast(message)
                    if (fromRow == 0) {
                        addHeaderComment(0)
                    }
                }
            }
        }
    }

    private fun toggleProgress(fromRow: Int = 0, state: Boolean) {
        if (fromRow > 0) {
            if (state) {
                content_course_comment_progress_bottom?.visibility = View.VISIBLE
                content_course_comment_progress_bottom?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                content_course_comment_progress_bottom?.layoutParams?.height = 1
                content_course_comment_progress_bottom?.visibility = View.INVISIBLE
            }
        } else {
            content_course_comment_progress_bottom?.layoutParams?.height = 1
            content_course_comment_progress_bottom?.visibility = View.INVISIBLE
        }
        content_course_comment_progress_bottom?.requestLayout()
        content_course_comment_rv?.requestLayout()

    }

    private fun addHeaderComment(numberOfComments: Int) {
        /*if(courseDetailAdapter.getItemCount(0) <= modulePlacementPos ){
            return
        }*/
        val indexToAdd = if (modulePlacementPos > 0) {
            modulePlacementPos + 2
        } else {
            1
        }
        //val mppPlus = modulePlacementPos + 1
        courseDetailAdapter.add(indexToAdd, HeaderCourseDetailComment(numberOfComments) {
            //showToast("Should show bottom dialog comment")
            val bottomSheetComment = FragmentBottomSheetComment.newInstance()
            bottomSheetComment.currentAvatar = MLPrefModel.userImageLarge
            bottomSheetComment.callbackComment = { comment ->
                bottomSheetComment.dismiss()
                var textHtml = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textHtml = Html.toHtml(SpannedString(comment), Html.TO_HTML_PARAGRAPH_LINES_CONSECUTIVE)
                } else {
                    textHtml = Html.toHtml(SpannedString(comment))
                }
                textLoader?.text = "Submit comment..."
                progressDialog?.show()
                courseViewModel.postComment(textHtml, courseId) { isValidToken, errorState, message, response ->
                    progressDialog?.dismiss()
                    showToast(message)
                    if (!isValidToken) {
                        navigateTo<LoginActivity>(true)
                    } else {
                        if (!errorState) {
                            //should reload the comment list
                            //val tempHeader = courseDetailAdapter.getItem(0)
                            //courseDetailAdapter.add(tempHeader)
                            resetHeadersContent()
                            resetViewModelLimit()
                            textLoader?.text = "Loading latest comments..."
                            progressDialog?.show()
                            getComments(0, MLConfig.LimitData, courseId)
                        }

                    }
                }
            }
            bottomSheetComment.show(supportFragmentManager, "bottom_sheet_comment")
        })
    }

    private fun resetHeadersContent() {
        var tempItems = arrayListOf<Item<ViewHolder>>()
        tempItems.add(courseDetailAdapter.getItem(0))
        if (modulePlacementPos >= 1) {
            tempItems.add(courseDetailAdapter.getItem(1))
            for (cdIndexMod in 2 until modulePlacementPos + 2) {
                try {
                    tempItems.add(courseDetailAdapter.getItem(cdIndexMod))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        courseDetailAdapter.clear()
        courseDetailAdapter.addAll(tempItems)
    }

    private fun showRatingDialog(rowsCourseDetail: RowsCourseDetail) {
        //fixme better if using endpoint to check if user already rate
        MLPrefModel.key = "rating-$courseId"
        if (MLPrefModel.alreadyRating == courseId) return
        val ratingDialog = AlertDialog.Builder(this).create()
        val ratingDialogLayout = layoutInflater.inflate(R.layout.dialog_rating, null)
        val ratingCancel = ratingDialogLayout?.findViewById<Button>(R.id.rating_cancel_btn)
        var rating = 0
        ratingCancel?.setOnClickListener {
            ratingDialog?.dismiss()
        }

        val ratingSubmit = ratingDialogLayout?.findViewById<Button>(R.id.rating_submit_btn)
        ratingSubmit?.setOnClickListener {
            ratingDialog?.dismiss()
            textLoader?.text = "submit rating..."
            progressDialog?.show()
            courseViewModel?.postRating(courseId, rating) { isValidToken, error, message, response ->
                progressDialog?.dismiss()
                if (!isValidToken) {
                    navigateTo<LoginActivity>(true)
                } else {
                    //fixme better if using endpoint to check if user already rate
                    if (!error || message.contains("already rate")) {
                        MLPrefModel.alreadyRating = courseId
                    }
                    showToast(message)
//                    getCourseRating(courseId)
                    getCourseDetail()
                }
            }
        }

        val ratingBar = ratingDialogLayout?.findViewById<AppCompatRatingBar>(R.id.rating_bar)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ratingBar?.let { rb ->
                try {
                    rb.progressDrawable?.let { progressDrawable ->
                        DrawableCompat.setTint(
                            progressDrawable,
                            ContextCompat.getColor(rb.context, R.color.colorPrimary)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
        ratingBar?.setOnRatingBarChangeListener { _, fl, _ ->
            rating = fl.toInt()
            MLLog.showLog(classTag, "$fl")
        }
        ratingDialogLayout?.let {
            ratingDialog?.setView(it)
            ratingDialog?.setCancelable(false)
        }
        if (!isFinishing) {
            ratingDialog?.show()
        }
    }

    private fun getCourseRating(courseId: Int) {
        textLoader?.text = "refresh rating ..."
        progressDialog?.show()
        courseViewModel.getRating(courseId) { isvalidToken, isErrorReq, errorMessage, response ->
            progressDialog?.dismiss()
            if (!isvalidToken) {
                navigateTo<LoginActivity>(true)
            } else {
                if (isErrorReq) {

                } else {
                    //get ref of header where star rating exists
                    val headerRating = courseDetailAdapter.getItem(0)
                    if (headerRating is HeaderCourseDetail) {
                        headerRating.row.rating = response?.rating ?: 0.toDouble()
                        headerRating.reloadRating()
                    }

                }
            }
        }
    }

    @Subscribe
    fun onEvent(commentEvent: HaloEvent){
        Toast.makeText(this, "Menerima ${commentEvent.teks}", Toast.LENGTH_SHORT).show()
        Log.d("eventComment :", commentEvent.teks)
    }

}
