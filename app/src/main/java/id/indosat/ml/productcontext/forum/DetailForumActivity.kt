package id.indosat.ml.productcontext.forum
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemCommentForum
import id.indosat.ml.adaptercomponent.ItemDetailForumHeader
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.*
import id.indosat.ml.common.ml_enum.MLEEditorPurpose
import id.indosat.ml.common.ml_enum.MLESortStringType
import id.indosat.ml.common.model.response.ReportDiscussion
import id.indosat.ml.common.model.response.ResultForumComment
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLForumViewModel
import kotlinx.android.synthetic.main.activity_detail_forum.*


class DetailForumActivity : BaseActivity() {
    companion object {
        const val detailTitle = "forum_detail_title"
        const val detailId = "forum_detail_id"
        const val detailSortBy = "forum_detail_sortby"
        const val detailCategory = "forum_detail_category"
        const val canEdit = "can-delete"
        const val canDelete = "can-delete"
    }

    private lateinit var forumViewModel: MLForumViewModel
    private var prevId = 0
    private var nextId = 0
    private var currentId = 0

    private var detailForumAdapter = GroupAdapter<ViewHolder>()
    private lateinit var llm:LinearLayoutManager

    private var _discussionId:Int=0
    private var _postID:Int = 0
    private var sortBy = ""
    private var category = ""
    private var _sortCommentsMethod = MLESortStringType.DESCENDING_DATE.type
    private var isLastPage = false
    private var isLoading = false
    private var isSubscribed = false
    private var isLiked = false
    private var canedit = false
    private var candelete = false
    private var categoryPost = ""
    private var textLoader: TextView?=null
    private var progressDialogSubsLike: AlertDialog?=null
    private var progressDialog: AlertDialog?=null
    private var reportForum: Int = 6000

    override fun onDestroy() {
        super.onDestroy()
        forumViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
//        setRefreshLayout()
        navigateToForumDetail("",_discussionId,sortBy,category, canedit,candelete )
    }



    override fun onRestart() {
        super.onRestart()
        setRefreshLayout()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_detail_forum)
        supportActionBar?.title = "Loading..."
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        //initiate view model
        forumViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLForumViewModel::class.java)
        /*intent.getStringExtra(detailTitle)?.let {
            supportActionBar?.title = it
        }*/
        _discussionId = intent.getIntExtra(detailId,0)
        canedit = intent.getBooleanExtra(canEdit,false)
        candelete = intent.getBooleanExtra(canDelete,false)

        setProgressDialog()
        setRefreshLayout()
        setClickListener()
        checkNextPrevState()
        prepList()
        getDetailRequest(_discussionId)
    }


    private fun setRefreshLayout(){
        detail_forum_refresh_layout?.setOnRefreshListener {
            detail_forum_refresh_layout?.isRefreshing = false
            getDetailRequest(_discussionId)
        }
    }

    private fun prepList(){
        llm = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        detail_forum_rv?.itemAnimator = null
        detail_forum_rv?.layoutManager = llm
        detail_forum_rv?.adapter = detailForumAdapter
        detail_forum_rv?.addOnScrollListener(object:PaginationGLScrollListener(llm){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                this.checkScrolled()
            }

            override fun loadMoreItems() {
                if(forumViewModel.totalDataForumComment <= forumViewModel.fromRowForumComment){return}
                getComments(_discussionId,_sortCommentsMethod,forumViewModel.fromRowForumComment,MLConfig.LimitData)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    private fun setClickListener(){
        checkNextPrevState()
        detail_forum_next?.setOnClickListener {
            //showToast("NEXT CLICKED")
            getDetailRequest(nextId)

        }

        detail_forum_prev?.setOnClickListener {
            //showToast("PREV CLICKED")
            getDetailRequest(prevId)
        }

        detail_forum_post_button?.setOnClickListener {
            detail_forum_qrep_et?.text?.let {qrep ->
                if(qrep.toString().isEmpty()){
                    showToast("Cannot post empty comment!")
                }else{
                    //showToast("should post comment")
                    detail_forum_qrep_et?.setText("")
                    detail_forum_qrep_et?.clearFocus()
                    val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    keyboard?.hideSoftInputFromWindow(detail_forum_qrep_et.windowToken, 0)
                    postComment("reply for PostID:$_postID",qrep.toString())
                }
            }
        }

        detail_forum_adv_button?.setOnClickListener {
            navigateToAdvanceEditor(mutableMapOf(AdvanceEditor.editorPurposeKey to MLEEditorPurpose.REPLY.type,
                AdvanceEditor.editorTitleKey to (supportActionBar?.title ?: "").toString()),
                mutableMapOf(AdvanceEditor.editorForumIdKey to currentId,AdvanceEditor.editorForumPostIdKey to _postID),0,"","", "",_discussionId,"",false)
        }
    }

    private fun checkNextPrevState(){
        isSubscribed = false
        isLoading = false
        isLastPage = false
        isLiked = false
        if(nextId > 0){
            detail_forum_next?.isEnabled = true
            detail_forum_next?.setTextColor(ActivityCompat.getColor(this,R.color.black))
            //detail_forum_next?.compoundDrawableTintList = ContextCompat.getColorStateList(this,R.color.black)
            //detail_forum_next?.backgroundTintList = ContextCompat.getColorStateList(this,R.color.black)
        }else{
            detail_forum_next?.isEnabled = false
            detail_forum_next?.setTextColor(ActivityCompat.getColor(this,R.color.light_gray))
        }

        if(prevId > 0){
            detail_forum_prev?.isEnabled = true
            detail_forum_prev?.setTextColor(ActivityCompat.getColor(this,R.color.black))
        }else{
            detail_forum_prev?.isEnabled = false
            detail_forum_prev?.setTextColor(ActivityCompat.getColor(this,R.color.light_gray))

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AdvanceEditor.AdvanceEditorCommentRequest && resultCode == Activity.RESULT_OK){
            data?.let {
                it.getStringExtra(AdvanceEditor.returnedCommentKey)?.let {comment->
                    postComment("reply for PostID:$_postID",comment)
                }
            }
        } else if(requestCode == reportForum && resultCode == Activity.RESULT_OK){
            data?.let {
                it.getStringExtra("data")?.let {data->
                    showToast(data)
                }
            }
        }
        getDetailRequest(_discussionId)
    }

    private fun postComment(subject:String,message:String){
        detail_forum_post_button?.visibility = View.GONE
        detail_forum_qrep_post_progress?.visibility = View.VISIBLE
        forumViewModel.postSimpleComment(_postID,subject,message){isValidToken,isError,errorMessage,response->
            detail_forum_post_button?.visibility = View.VISIBLE
            detail_forum_qrep_post_progress?.visibility = View.GONE
            if(!isValidToken){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                if(!isError){
                    showToast("posted")
                    getComments(_discussionId,_sortCommentsMethod,0)
                }else{
                    showToast(errorMessage)
                }
            }
        }
    }

    private fun postLike(_postID: Int){
        toggleButtonHeader(false)
        textLoader?.text = "Sending like request..."
        progressDialogSubsLike?.show()
        forumViewModel.sendLikeForum(_discussionId,_postID, if(isLiked) 0 else 1){isValidToken,errorState,message,response->
            progressDialogSubsLike?.dismiss()
            if(!isValidToken){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    checkIsForumLiked()
                }
                showToast(message)
            }
            toggleButtonHeader(true)
        }
    }

    private fun postSubscribe(){
        toggleButtonHeader(false)
        textLoader?.text = "Sending subscribe request..."
        progressDialogSubsLike?.show()
        forumViewModel.postSubscribeForum(_discussionId){isValidToken,errorState,errorMessage, response->
            progressDialogSubsLike?.dismiss()
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                response?.let {
                    isSubscribed = true
                    checkIsForumSubscribed(_discussionId)
                }?:run{
                    showToast(errorMessage)
                }
            }
            toggleButtonHeader(true)
        }
    }

    private fun postUnSubs(){
        toggleButtonHeader(false)
        textLoader?.text = "Sending unsubscribe request..."
        progressDialogSubsLike?.show()
        forumViewModel.postUnSubscribeForum(_discussionId){isValidToken,errorState,errorMessage, response->
            progressDialogSubsLike?.dismiss()
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                response?.let {
                    isSubscribed = false
                    checkIsForumSubscribed(_discussionId)
                }?:run{
                    showToast(errorMessage)
                }
            }
            toggleButtonHeader(true)
        }
    }

    private fun getDetailRequest(detailId:Int){
        _discussionId = detailId
        _postID = 0
        sortBy = intent.getStringExtra(detailSortBy)?:""
        category = intent.getStringExtra(detailCategory)?:""
        toggleRequestSideEff(false)
        detailForumAdapter.clear()
        supportActionBar?.title = "Loading..."
        forumViewModel.getDetailForum(detailId,sortBy,category){isTokenValid,errorState,message,response->
            toggleRequestSideEff(true)
            if(!isTokenValid){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    showToast(message)
                    supportActionBar?.title = ""
                }else{
                    response?.let {resp->
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            supportActionBar?.title = Html.fromHtml(resp.rows.name, Html.FROM_HTML_MODE_LEGACY).toString()
                        }else{
                            supportActionBar?.title = Html.fromHtml(resp.rows.name).toString()
                        }
                        prevId = resp.previous
                        nextId = resp.next
                        currentId = resp.rows.id
                        _postID = resp.rows.id
                        categoryPost = resp.rows.category
                        checkNextPrevState()
                        detailForumAdapter.add(ItemDetailForumHeader(resp,canedit,candelete,
                            callBackLike = {
                                postLike(_postID)
                        },callBackSubscribe = {
                                if(isSubscribed){
                                    postUnSubs()
                                }else{
                                    postSubscribe()
                                }
                        },callBackReport = {
                                val report = ReportDiscussion(discussionid = _discussionId, id = _postID, message = resp.rows.message, subject = resp.rows.name)
                                navigateToWithResult<ReportActivity>(reportForum, report)
                        },callBackEdit = {
                                navigateToAdvanceEditor(mutableMapOf(AdvanceEditor.editorPurposeKey to MLEEditorPurpose.NEW.type,
                                    AdvanceEditor.editorTitleKey to (supportActionBar?.title ?: "").toString()),
                                    mutableMapOf(AdvanceEditor.editorForumIdKey to 0,AdvanceEditor.editorForumPostIdKey to 0),
                                    AdvanceEditor.AdvanceEditorNewRequest, "", GsonBuilder().create().toJson(resp), categoryPost, _discussionId,"",false)},
                            callBackDelete = {
                                deleteDiscussion(_discussionId.toString())
                            }))
                        getComments(_discussionId,_sortCommentsMethod)
                        checkIsForumSubscribed(_discussionId)
                    }?:run {
                        showToast(message)
                        supportActionBar?.title = ""
                    }
                }
            }
        }
    }



    private fun toggleButtonHeader(state:Boolean){
        if(detailForumAdapter.itemCount > 0){
            val firstHeader = detailForumAdapter.getItem(0)
            if(firstHeader is ItemDetailForumHeader){
                firstHeader.toggleButtonEnabled(state)
                //detailForumAdapter.notifyItemChanged(0)
            }
        }
    }

    private fun checkIsForumSubscribed(discussionId: Int){
        forumViewModel.getSubscribedStatus(discussionId){isValidToken,errorReq,message,response->
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorReq && response != null){
                    if(detailForumAdapter.itemCount > 0){
                        val firstHeader = detailForumAdapter.getItem(0)
                        if(firstHeader is ItemDetailForumHeader){
                            isSubscribed = true
                            firstHeader.setIsSubscribed(true)
                            detailForumAdapter.notifyItemChanged(0)
                        }
                    }
                }else{
                    if(detailForumAdapter.itemCount > 0){
                        val firstHeader = detailForumAdapter.getItem(0)
                        if(firstHeader is ItemDetailForumHeader){
                            isSubscribed = false
                            firstHeader.setIsSubscribed(false)
                            detailForumAdapter.notifyItemChanged(0)
                        }
                    }
                }
                //toggleButtonHeader(true)
                //getComments(_discussionId,_sortCommentsMethod)
                checkIsForumLiked()
                /*if(detailForumAdapter.itemCount > 0){
                    val firstHeader = detailForumAdapter.getItem(0)
                    if(firstHeader is ItemDetailForumHeader){
                        firstHeader.setIsSubscribed(true)
                        detailForumAdapter.notifyItemChanged(0)
                        //detailForumAdapter.notifyDataSetChanged()
                    }
                }*/
            }
        }
    }


    private fun checkIsForumLiked(){
        forumViewModel.getLikeStatus(_discussionId,_postID){isValidToken,errorState,message,response->
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState && response != null){
                    //this user like this discussion
                    if(detailForumAdapter.itemCount > 0){
                        val firstHeader = detailForumAdapter.getItem(0)
                        if(firstHeader is ItemDetailForumHeader){
                            isLiked = true
                            firstHeader.setIsLiked(true)
                            detailForumAdapter.notifyItemChanged(0)
                        }
                    }
                }else{
                    //user not setting like for this discussion
                    if(detailForumAdapter.itemCount > 0){
                        val firstHeader = detailForumAdapter.getItem(0)
                        if(firstHeader is ItemDetailForumHeader){
                            isLiked = false
                            firstHeader.setIsLiked(false)
                            detailForumAdapter.notifyItemChanged(0)
                        }
                    }
                }
                toggleButtonHeader(true)
                //getComments(_discussionId,_sortCommentsMethod)
            }
        }
    }

    private fun getComments(discussionId:Int,sortCommentsMethod:String,fromRow:Int=0,limitRow:Int= MLConfig.LimitData){
        isLoading = true
        if(fromRow == 0){
            toggleBottomProgress(true)
            forumViewModel.resetDataCounterComment()
        }else{
            toggleBottomProgress(true)
        }
        forumViewModel.getForumComments(discussionId,sortCommentsMethod,fromRow,limitRow){isValidToken,
                                                                                          errorReq,
                                                                                          isThisLast,
                                                                                          messageReq,
                                                                                          response->
            isLoading = false
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                isLastPage = isThisLast
                toggleBottomProgress(false)
                if(!errorReq){
                    response?.let {resp->
                        if(fromRow == 0){
                            if(detailForumAdapter.itemCount >= 1){
                                val tempHeaderItem = detailForumAdapter.getItem(0)
                                detailForumAdapter.clear()
                                detailForumAdapter.add(tempHeaderItem)
                            }
                        }
                        parseCommentToAdapter(resp)
                        llm.requestLayout()
                    }
                    MLLog.showLog(classTag,"got forum comments response")
                }else{
                    showToast(messageReq)
                }
            }

        }
    }


    private fun parseCommentToAdapter(resp:ResultForumComment){
        var currentPos = detailForumAdapter.itemCount
        if(resp.rows.isNotEmpty()){
            for(row in resp.rows){
                if(row.id != _postID) {
                    detailForumAdapter.add(ItemCommentForum(row, row.can_edit,row.can_delete,currentPos, resp,
                        callBackEdit = {
                            navigateToAdvanceEditor(mutableMapOf(AdvanceEditor.editorPurposeKey to MLEEditorPurpose.NEW.type,
                                AdvanceEditor.editorTitleKey to (supportActionBar?.title ?: "").toString()),
                                mutableMapOf(AdvanceEditor.editorForumIdKey to 0,AdvanceEditor.editorForumPostIdKey to 0),
                                AdvanceEditor.AdvanceEditorNewRequest, GsonBuilder().create().toJson(row), "", categoryPost,_discussionId,row.id.toString(),true)
                    }, callBackDelete = {
                            deletePost(row.id.toString())
                        }, callBackReport = {
                            val report = ReportDiscussion(row.discussionid, row.id, row.message, row.subject)
                            navigateToWithResult<ReportActivity>(reportForum, report)
                        }, callBackLike = { it ->
                            postLike(row.id)
                        } ))
                    currentPos += 1
                }
            }
        }
    }

    private fun deleteDiscussion(discussionId:String){
        textLoader?.text = "Deleting discussion,please wait..."
        progressDialog?.show()
        forumViewModel.deleteDiscussion(discussionId){ isTokenValid, errorState, message, result->
            progressDialog?.dismiss()
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    //finish()
                    val commentIntent = Intent()
                    commentIntent.putExtra(AdvanceEditor.returnedCommentKey,message)
                    setResult(Activity.RESULT_OK,commentIntent)
                    finish()
                }else if(result == null){
                    Toast.makeText(this, "Something Wrong, Please try again later", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Something Wrong, Please try again later", Toast.LENGTH_SHORT).show()
                }


            }
        }
    }

    private fun deletePost(postId:String){
        textLoader?.text = "Deleting discussion,please wait..."
        progressDialog?.show()
        isLoading = true
        Toast.makeText(this, "Loading....", Toast.LENGTH_SHORT).show()
        forumViewModel.deletePost(postId){ isTokenValid, errorState, message, result->
            progressDialog?.dismiss()
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    //finish()
                    isLoading = false
                    getDetailRequest(_discussionId)
                }else if(result == null){
                    isLoading = false
                    Toast.makeText(this, "Something Wrong, Please try again later", Toast.LENGTH_SHORT).show()
                }else{
                    isLoading = false
                    Toast.makeText(this, "Something Wrong, Please try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun toggleRequestSideEff(state:Boolean){
        detail_forum_progress?.visibility = if(!state) View.VISIBLE else View.GONE
        detail_forum_prev?.isEnabled = state
        detail_forum_next?.isEnabled = state
        detail_forum_adv_button?.isEnabled = state
        detail_forum_post_button?.isEnabled = state
        detail_forum_qrep_et?.setText("")
    }

    private fun toggleBottomProgress(state:Boolean){
        if(state){
            detail_forum_bottom_progress?.visibility = View.VISIBLE
            detail_forum_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }else{
            detail_forum_bottom_progress?.layoutParams?.height = 1
            detail_forum_bottom_progress?.visibility = View.INVISIBLE
        }
        detail_forum_bottom_progress?.requestLayout()
        detail_forum_rv?.requestLayout()
    }


    private fun setProgressDialog(){
        val pairOf = getProgressDialog()
        progressDialogSubsLike = pairOf.first
        textLoader = pairOf.second.findViewById(R.id.progress_dialog_loading_text)
    }
}
