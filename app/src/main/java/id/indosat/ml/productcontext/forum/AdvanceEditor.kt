package id.indosat.ml.productcontext.forum

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.getProgressDialog
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.ml_enum.MLEEditorPurpose
import id.indosat.ml.common.model.response.ResultDFR
import id.indosat.ml.common.model.response.RowComments
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLForumViewModel
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_advance_editor.*

class AdvanceEditor : BaseActivity() {
    companion object {
        const val editorPurposeKey = "editor-purpose-key"
        const val editorTitleKey = "editor-title-key"
        const val editorForumIdKey = "editor-forum-id-key"
        const val editorForumPostIdKey = "editor-forum-post-id-key"
        const val returnedCommentKey = "returned-comment-adv"
        const val AdvanceEditorCommentRequest = 199
        const val AdvanceEditorNewRequest = 201
        const val resultComment = "result-comment"
        const val resultHeader = "result-header"
        const val categoryPost = "category-post"
        const val discussionID = "discussion-id"
        const val postIdComment = "post-id"
        const val isComment = "is-comment"
    }
    private var title = ""
    private var commentTitle = ""
    private var forumId = 0
    private var postId = 0
    private var postCommentId = ""
    private var isCommentFlag = false
    private var discID = ""
    private var resComment = ""
    private var resHeader = ""
//    private var discussionID = ""
    private var categoryPostSpinner = ""
    private lateinit var resultCommentResp : RowComments
    private lateinit var resultHeaderResp : ResultDFR
    private var activeEditor = 1 //0 for title, 1 for message
    private lateinit var forumViewModel: MLForumViewModel
    private var purpose:MLEEditorPurpose = MLEEditorPurpose.UNDEFINED
    private var spinnerAdapter:ArrayAdapter<String>?=null
    private lateinit var spinnerCat:Array<String>
    private var selectedCat = ""
    private var textLoader: TextView?=null
    private var progressDialog: AlertDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        forumViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLForumViewModel::class.java)
        setContentView(R.layout.activity_advance_editor)


        forumId = intent.getIntExtra(editorForumIdKey,0)
        postId = intent.getIntExtra(editorForumPostIdKey,0)
        discID = intent.getStringExtra(discussionID)
        resHeader = intent.getStringExtra(resultHeader)
        resComment = intent.getStringExtra(resultComment)
        categoryPostSpinner = intent.getStringExtra(categoryPost)
        postCommentId = intent.getStringExtra(postIdComment)
        isCommentFlag = intent.getBooleanExtra(isComment, false)
//        resultHeaderResp = intent.getSerializableExtra(resultHeader) as ResultDFR
//        resultCommentResp = intent.getSerializableExtra(resultComment) as RowComments


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.elevation = 0f
        intent.getStringExtra(editorPurposeKey).let{
            val purposeString = if(it == null) "" else it
            if(purposeString.toLowerCase() == MLEEditorPurpose.REPLY.type.toLowerCase()){
                supportActionBar?.title = "Reply for $title"
                editor_title_container?.visibility = View.GONE
                purpose = MLEEditorPurpose.REPLY
            }else if(purposeString.toLowerCase() == MLEEditorPurpose.NEW.type.toLowerCase()){
                supportActionBar?.title = "New Discussion"
                editor_title_view?.visibility = View.VISIBLE
                purpose = MLEEditorPurpose.NEW
                setProgressDialog()
                setSpinner()
                getCategories()
            }else{
                showToast("Unimplemented editor purpose")
                //finish()
            }
            editor_title_container?.requestLayout()
            editor_view?.requestLayout()
        }
        if(!resHeader.isNullOrEmpty()){
            val modelResultHeader : ResultDFR = GsonBuilder().create()
                .fromJson(resHeader, object : TypeToken<ResultDFR>() {}.type)
            setEditableContent(modelResultHeader, null)
        }else if (!resComment.isNullOrEmpty()){
            val modelResultComment : RowComments = GsonBuilder().create()
                .fromJson(resComment, object : TypeToken<RowComments>() {}.type)
            setEditableContent(null, modelResultComment)
        }

        if(isCommentFlag){
            editor_title_view.isEnabled = false
            editor_category.isEnabled = false
        }

        setEditor()
        if(purpose == MLEEditorPurpose.NEW){
            //request for categories discusssion
        }
    }





    /*var plainTextHTMl = ""
    var addedTagHTML = ""
    private var isBold = false
    private var isItalic = false
    private var isAlignLeft = false*/

    private fun setBaseFor(editor:RichEditor?){
        editor?.setLayerType(View.LAYER_TYPE_HARDWARE,null)
        editor?.setInitialScale(1)
        editor?.setPlaceholder("Insert your comment here...")
        editor?.settings?.layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
        editor?.settings?.javaScriptEnabled = true
        editor?.settings?.loadWithOverviewMode = true
        editor?.settings?.useWideViewPort = true
        editor?.setPadding(16 ,16,16,16)
        editor?.setEditorFontSize(50)
    }

    private fun setListenerEditor(editor:RichEditor?){
        editor?.setOnTextChangeListener(object:RichEditor.OnTextChangeListener{
            override fun onTextChange(text: String?) {
                editor_view_validate?.visibility = View.GONE
            }
        })
        editor?.setOnFocusChangeListener { view, b ->
            if (b) editor_view_validate?.visibility = View.GONE
        }
        editor_view_validate?.setOnKeyListener(View.OnKeyListener { view, i, keyEvent ->
            editor_view_validate?.visibility = View.GONE
            editor?.requestFocus()
            false
        })

        editor_view_validate?.setOnFocusChangeListener { view, b ->

        }

        editor_bold?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setBold()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0) {
                    //editor_title_view?.setBold()
                }else{
                    editor_view?.setBold()
                }
            }

        }
        editor_italic?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setItalic()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0){
                    //editor_title_view?.setItalic()
                }else{
                    editor_view?.setItalic()
                }

            }
        }
        editor_align_left?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setAlignLeft()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0) {
                    //editor_title_view?.setAlignLeft()
                }else{
                    editor_view?.setAlignLeft()
                }
            }
        }

        editor_align_center?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setAlignCenter()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0){
                    //editor_title_view?.setAlignCenter()
                }else{
                    editor_view?.setAlignCenter()
                }

            }
        }

        editor_align_right?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setAlignRight()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0) {
                    //editor_title_view?.setAlignRight()
                }else{
                    editor_view?.setAlignRight()
                }
            }
        }

        editor_strike?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setStrikeThrough()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0){
                    //editor_title_view?.setStrikeThrough()
                }else{
                    editor_view?.setStrikeThrough()
                }

            }

        }

        editor_underline?.setOnClickListener {
            if(purpose == MLEEditorPurpose.REPLY){
                editor_view?.setUnderline()
            }else if(purpose == MLEEditorPurpose.NEW){
                if(activeEditor == 0){
                    //editor_title_view?.setUnderline()
                }else{
                    editor_view?.setUnderline()
                }

            }
        }
    }

    private fun setEditableContent(resultHeader: ResultDFR?, rowComments: RowComments?){
        if(resultHeader == null && rowComments == null){
            //Nothing
            intent.getStringExtra(editorTitleKey).let {
                title = it ?: ""
            }
        }else{
            title = intent.getStringExtra(editorTitleKey)
            editor_title_view.setText(title)
            if(resultHeader != null){
                editor_view?.html = resultHeader.rows.message
            }else{
                editor_view?.html = rowComments?.message
                editor_title_view.setText("Re : "+rowComments?.subject)
                commentTitle = rowComments?.subject!!
            }

        }
    }

    private fun setEditor(){
        //editor Title view
        //setBaseFor(editor_title_view)
        setBaseFor(editor_view)

        editor_view?.setPlaceholder("Insert message here...")
        //editor_title_view?.setPlaceholder("Insert title here...")
        editor_title_view?.hint = "Insert title here..."
        editor_view?.setOnTouchListener { view, motionEvent ->
            activeEditor = 1
            editor_button_container?.visibility = View.VISIBLE
            false
        }

        editor_title_view?.setOnTouchListener { view, motionEvent ->
            activeEditor = 0
            editor_button_container?.visibility = View.GONE
            false
        }
        setListenerEditor(editor_view)
    }
    private fun stripAndAddHTML(stringHTML:String){
        if(!stringHTML.contains("<html>")){
            editor_view?.html = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><style>body{font-size:18pt;}</style></head>$stringHTML</html>"
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.advance_editor_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            if(it.itemId == R.id.nav_send_message){
                sendText()
                //MLLog.showLog(classTag,"should validate comment and post it")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendText(){
        if(purpose == MLEEditorPurpose.REPLY){
            val htmlToSend = editor_view?.html ?: ""
            if(htmlToSend.isEmpty()){
                editor_view_validate?.requestFocus()
                editor_view_validate?.visibility = View.VISIBLE
                editor_view_validate?.error = "Comment cannot be empty"
            }else{
                MLLog.showLog(classTag,htmlToSend)
                //postComment("reply for PostID:$postId",htmlToSend)
                val commentIntent = Intent()
                commentIntent.putExtra(returnedCommentKey,htmlToSend)
                setResult(Activity.RESULT_OK,commentIntent)
                finish()
            }
        }else if(purpose == MLEEditorPurpose.NEW){
            if (selectedCat.isEmpty()){
                showToast("Category required")
                return
            }
            val title = editor_title_view?.text?.toString() ?: ""
            if(title.isEmpty()){
                editor_title_view?.error = "Title required"
                return
            }

            val content = editor_view?.html ?: ""
            if(content.isEmpty()){
                editor_view_validate?.requestFocus()
                editor_view_validate?.visibility = View.VISIBLE
                editor_view_validate?.error = "Content required"
                //editor_view_validate?.clearFocus()
                return
            }
            if(!resComment.isNullOrEmpty() || !resHeader.isNullOrEmpty()){
                if(isCommentFlag){
                    postEditComment(postCommentId,commentTitle,content)
                }else{
                    postEditDiscussion(discID,selectedCat,title,content)
                }
            }else{
                postCreateDiscussion(selectedCat,title,content)
            }
        }

    }

    private fun setSpinner(){
        spinnerCat = arrayListOf("Commerce","Enterprise","Innovation","ISAC","Leadership","Technology").toTypedArray()
        selectedCat = spinnerCat.first()
        editor_category?.onItemSelectedListener  = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, id: Long) {
                selectedCat = spinnerCat[pos]
            }
        }
    }


    private fun getCategories(){
        forumViewModel.getDiscussionCategories{isTokenValid,errorState,message,result->
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    showToast(message)
                    finish()
                }else{
                    result?.let {
                        spinnerCat = it.toTypedArray()
                        selectedCat = spinnerCat.first()

                    }
                    spinnerAdapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,spinnerCat)
                    spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerAdapter?.let {spa->
                        editor_category?.adapter = spa
                        if(categoryPostSpinner.isEmpty()){
                            // Nothing
                        }else{
                            if(categoryPostSpinner.equals("Commerce", true)) editor_category.setSelection(0)
                            else if (categoryPostSpinner.equals("Enterprise", true)) editor_category.setSelection(1)
                            else if (categoryPostSpinner.equals("Innovation", true)) editor_category.setSelection(2)
                            else if (categoryPostSpinner.equals("ISAC", true)) editor_category.setSelection(3)
                            else if (categoryPostSpinner.equals("Leadership", true)) editor_category.setSelection(4)
                            else if (categoryPostSpinner.equals("Technology", true)) editor_category.setSelection(5)
                            editor_category.isSelected = false
                        }
                    }

                }
            }
        }
    }
    private fun postCreateDiscussion(category:String,title:String,content:String){
        textLoader?.text = "Posting discussion,please wait..."
        progressDialog?.show()
        forumViewModel.postCreateDiscussion(category,title,content){isTokenValid,errorState,message,result->
            progressDialog?.dismiss()
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    //finish()
                    val commentIntent = Intent()
                    commentIntent.putExtra(returnedCommentKey,message)
                    setResult(Activity.RESULT_OK,commentIntent)
                    finish()
                }
            }
        }
    }

    private fun postEditDiscussion(discussionId:String, category:String,title:String,content:String){
        textLoader?.text = "Posting discussion,please wait..."
        progressDialog?.show()
        forumViewModel.postEditDiscussion(discussionId,category,title,content){ isTokenValid, errorState, message, result->
            progressDialog?.dismiss()
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    //finish()
                    val commentIntent = Intent()
                    commentIntent.putExtra(returnedCommentKey,message)
                    setResult(Activity.RESULT_OK,commentIntent)
                    finish()
                }
            }
        }
    }

    private fun postEditComment(postId:String, title:String,content:String){
        textLoader?.text = "Posting discussion,please wait..."
        progressDialog?.show()
        forumViewModel.updatePost(postId,"",title,content){ isTokenValid, errorState, message, result->
            progressDialog?.dismiss()
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(!errorState){
                    //finish()
                    val commentIntent = Intent()
                    commentIntent.putExtra(returnedCommentKey,message)
                    setResult(Activity.RESULT_OK,commentIntent)
                    finish()
                }
            }
        }
    }


    private fun setProgressDialog(){
        val pairOf = getProgressDialog(false)
        progressDialog = pairOf.first
        textLoader = pairOf.second.findViewById(R.id.progress_dialog_loading_text)
    }
}
