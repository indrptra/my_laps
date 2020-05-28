package id.indosat.ml.productcontext.forum

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemKnowledgeForum
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.getProgressDialog
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToAdvanceEditor
import id.indosat.ml.common.coordinator.navigateToForumDetail
import id.indosat.ml.common.ml_enum.MLEEditorPurpose
import id.indosat.ml.common.model.response.RowForum
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLForumViewModel
import id.indosat.ml.viewmodel.MLGeneralViewModel
import kotlinx.android.synthetic.main.app_bar_my_discussion_main.*
import kotlinx.android.synthetic.main.content_my_discussion_main.*
import kotlinx.android.synthetic.main.content_note.*

class MyDiscussionMainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var genViewModel: MLGeneralViewModel
    private lateinit var forumViewModel: MLForumViewModel
    private var isLastPage = false
    private var isLoading = false
    private lateinit var llm : LinearLayoutManager
    private val forumAdapter  = GroupAdapter<ViewHolder>()
    private var textLoader: TextView?=null
    private var progressDialog: AlertDialog?=null
    override fun onDestroy() {
        super.onDestroy()
        forumViewModel.clear()
        genViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_discussion_main)
        //setSupportActionBar(toolbar_my_discussion)
        supportActionBar?.title = "MY DISCUSSION"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fab_mydiscussion.setOnClickListener { view ->
            //showToast("Should go to form for create discussion")
            navigateToAdvanceEditor(mutableMapOf(AdvanceEditor.editorPurposeKey to MLEEditorPurpose.NEW.type,
                AdvanceEditor.editorTitleKey to (supportActionBar?.title ?: "").toString()),
                mutableMapOf(AdvanceEditor.editorForumIdKey to 0,AdvanceEditor.editorForumPostIdKey to 0),
                AdvanceEditor.AdvanceEditorNewRequest,"","","",0,"",false)
        }
        toolbar_back_button?.setOnClickListener {
            finish()
        }

        toolbar_filter_button?.setOnClickListener {
            showToast("Should do sorting here...")
        }

        toolbar_filter_button?.visibility = View.GONE
        setInitList()
        setViewModel()
        getMyDiscussion()
    }

    private fun setViewModel(){
        genViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
        forumViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLForumViewModel::class.java)
        forumViewModel.resetDataCounter()
    }

    private fun setInitList(){
        llm = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        mydiscusion_list_rv?.adapter = forumAdapter
        mydiscusion_list_rv?.layoutManager = llm
        mydiscusion_list_rv?.addOnScrollListener(object : PaginationGLScrollListener(llm){
            override fun loadMoreItems() {
                if(forumViewModel.totalDataForum <= forumViewModel.fromRowForum){return}
                getMyDiscussion(forumViewModel.fromRowForum)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.my_discussion_main, menu)
        //return true
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let{
            if(it.itemId == R.id.nav_right_filter){
                showToast("Should do sorting here...")
            }else if(it.itemId == android.R.id.home){
                finish()
                return true
            }else{
                return super.onOptionsItemSelected(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AdvanceEditor.AdvanceEditorNewRequest && resultCode == Activity.RESULT_OK){
            data?.let {
                it.getStringExtra(AdvanceEditor.returnedCommentKey)?.let {comment->
                    forumViewModel.resetDataCounter()
                    getMyDiscussion()
                }
            }
        }
    }


    private fun getMyDiscussion(fromRow:Int=0,limitRow:Int= MLConfig.LimitData){
        if(fromRow == 0){
            toggleBottomProgress(false)
            mydiscusion_list_rv?.visibility = View.GONE
            mydiscussion_progress_center?.visibility = View.VISIBLE
        }else{
            toggleBottomProgress(true)
        }
        isLoading = true
        forumViewModel.getMyDiscussion("","",fromRow,limitRow){
                isTokenValid,errorState,isThisLast,message,response->
            isLoading = false
            isLastPage = isThisLast
            toggleBottomProgress(false)
            if(fromRow == 0){
                mydiscussion_progress_center?.visibility = View.GONE
                mydiscusion_list_rv?.visibility = View.VISIBLE
            }
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    if(fromRow == 0){
                        toggleContentNote(true,message)
                    }else {
                        showToast(message)
                    }
                }else{
                    response?.let {resp->
                        //do process here
                        if(fromRow == 0){
                            forumAdapter.clear()
                            if(resp.rows.isEmpty()){
                                toggleContentNote(true,message)
                            }else{
                                mydiscussion_content_note?.visibility = View.GONE
                                mydiscusion_list_rv?.visibility = View.VISIBLE
                            }

                        }
                        if(resp.rows.isNotEmpty()){
                            forumAdapter.addAll(resp.rows.map { row->
                                ItemKnowledgeForum(row,true,{rowCallback->
                                    //showToast(rowCallback.name)
                                    navigateToForumDetail(rowCallback.name,rowCallback.discussionid,"","",false,false)

                                },{rowForum->
                                    setProgressDialog()
                                    closeDiscussion(rowForum)
                                })
                            })
                        }else{

                        }
                    }?:run {
                        if(fromRow == 0){
                            toggleContentNote(true,message)
                        }
                        //showToast(message)
                    }
                }
            }
        }
    }

    /*private fun toggleProgress(state:Boolean){
        progress_forum_sort?.visibility = if(state) View.VISIBLE else View.GONE
        progress_forum_cat?.visibility = if(state) View.VISIBLE else View.GONE
    }*/


    private fun toggleContentNote(state:Boolean,message:String=""){
        mydiscussion_content_note?.visibility = if(state)View.VISIBLE else{View.GONE}
        //mydiscussion_content_note?.findViewById<TextView>(R.id.content_note_text)?.text = message
        content_note_text?.text = message
        mydiscusion_list_rv?.visibility = if(state) View.GONE else View.VISIBLE
    }

    private fun toggleBottomProgress(state:Boolean){
        if(state){
            mydiscussion_list_bottom_progress?.visibility = View.VISIBLE
            mydiscussion_list_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }else{
            mydiscussion_list_bottom_progress?.layoutParams?.height = 1
            mydiscussion_list_bottom_progress?.visibility = View.INVISIBLE
        }
        mydiscussion_list_bottom_progress?.requestLayout()
        mydiscusion_list_rv?.requestLayout()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        //drawer_layout.closeDrawer(GravityCompat.END)
        return true
    }


    private fun setProgressDialog(){
        val pairOf = getProgressDialog(false)
        progressDialog = pairOf.first
        textLoader = pairOf.second.findViewById(R.id.progress_dialog_loading_text)
        textLoader?.text = "Closing discussion, please wait..."
    }

    private fun closeDiscussion(forum:RowForum){
        progressDialog?.show()
        forumViewModel.postCloseDiscussion(forum.discussionid){isValidToken,errorState, message,result->
            progressDialog?.dismiss()
            if(!isValidToken){
                navigateTo<LoginActivity>(true)
            }else{
                showToast(message)
                if(!errorState){
                    forumAdapter.clear()
                    forumViewModel.resetDataCounter()
                    getMyDiscussion()
                }
            }
        }
    }
}
