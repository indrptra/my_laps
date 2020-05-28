package id.indosat.ml.productcontext.forum

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main_knowledge_forum.*
import kotlinx.android.synthetic.main.app_bar_main_knowledge_forum.*
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemKnowledgeForum
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToAdvanceEditor
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.common.coordinator.navigateToForumDetail
import id.indosat.ml.common.ml_enum.MLEEditorPurpose
import id.indosat.ml.common.model.response.ResultForumFilter
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.productcontext.search.SearchForumActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLForumViewModel
import id.indosat.ml.viewmodel.MLGeneralViewModel
import kotlinx.android.synthetic.main.content_main_knowledge_forum.*

class MainKnowledgeForumActivity : BaseActivity(){

    private var checkedCatVal = ""
    private var checkedCatLabel = ""
    private var checkedSortVal = ""
    private var checkedSortLabel = ""
    private var isFilterSortApplied = false
    private var isFilterSortReseted = false
    private var isLastPage = false
    private var isLoading = false
    private lateinit var genViewModel:MLGeneralViewModel
    private lateinit var forumViewModel:MLForumViewModel
    private val forumAdapter  = GroupAdapter<ViewHolder>()
    private lateinit var llm : LinearLayoutManager
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
        setContentView(R.layout.activity_main_knowledge_forum)
        //setSupportActionBar(toolbar)
        supportActionBar?.title = "MY DISCUSSION"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val toggle =object:ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ){
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                MLLog.showLog(classTag,"close drawer")
            }
        }
        toggle.isDrawerIndicatorEnabled = false
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //nav_view.setNavigationItemSelectedListener(this)
        setInitList()
        setOnClick()
        setViewModel()
        getForumFilter()
        getKnowledgeForum()
    }

    private fun setViewModel(){
        genViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLGeneralViewModel::class.java)
        forumViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLForumViewModel::class.java)
        forumViewModel.resetDataCounter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getKnowledgeForum()
    }

    private fun setInitList(){
        forum_list_title?.text = ""
        llm = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        forum_list_rv?.adapter = forumAdapter
        forum_list_rv?.layoutManager = llm
        forum_list_rv?.addOnScrollListener(object : PaginationGLScrollListener(llm){
            override fun loadMoreItems() {
                if(forumViewModel.totalDataForum <= forumViewModel.fromRowForum){return}
                getKnowledgeForum(forumViewModel.fromRowForum)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })
    }


    private fun setOnClick(){
        fab_kf.setOnClickListener { _ ->
            navigateToAdvanceEditor(mutableMapOf(AdvanceEditor.editorPurposeKey to MLEEditorPurpose.NEW.type,
                AdvanceEditor.editorTitleKey to (supportActionBar?.title ?: "").toString()),
                mutableMapOf(AdvanceEditor.editorForumIdKey to 0,AdvanceEditor.editorForumPostIdKey to 0),
                AdvanceEditor.AdvanceEditorNewRequest,"","","",0,"",false)
        }
        toolbar_back_button_kf?.setOnClickListener {
            finish()
        }

        toolbar_search_button_kf?.setOnClickListener {
            //showToast("SHOULD PERFORM SEARCH FORUM")
            navigateTo<SearchForumActivity>()
        }

        toolbar_filter_button_kf?.setOnClickListener {
            drawer_layout?.let {drawer->
                if(drawer.isDrawerOpen(GravityCompat.END)){
                    drawer.closeDrawer(GravityCompat.END)
                }else{
                    drawer.openDrawer(GravityCompat.END)
                }
            }
        }

        forum_apply_button?.setOnClickListener {
            isFilterSortApplied = true
            drawer_layout?.closeDrawers()
            forumViewModel.resetDataCounter()
            llm.scrollToPositionWithOffset(0,0)
            getKnowledgeForum(0)
            if(checkedCatVal.isNotEmpty() && checkedSortVal.isEmpty()){
                forum_list_title?.text = "${checkedCatLabel.toUpperCase()} DISCUSSIONS"
            }
            if(checkedSortLabel.isNotEmpty()){
                forum_list_title?.text = "SORT BY ${checkedSortLabel.toUpperCase()} DISCUSSION"
            }
        }
        forum_reset_button?.setOnClickListener {
            isFilterSortReseted = true
            checkedCatVal = ""
            checkedSortVal = ""
            checkedSortLabel = ""
            forum_list_title?.text = "ALL DISCUSSIONS"
            forum_radio_button_categories_container?.clearCheck()
            forum_radio_button_sort_container?.clearCheck()
            drawer_layout?.closeDrawers()
            forumViewModel.resetDataCounter()
            getKnowledgeForum(0)
        }
    }

    override fun onResume() {
        super.onResume()
        getKnowledgeForum()
    }

    private fun getKnowledgeForum(fromRow:Int=0,limitRow:Int=MLConfig.LimitData){
        if(fromRow == 0){
            toggleBottomProgress(false)
            forum_list_rv?.visibility = View.GONE
            forum_list_progress_center?.visibility = View.VISIBLE
        }else{
            toggleBottomProgress(true)
        }
        isLoading = true
        forumViewModel.getKnowledgeForum(checkedSortVal,checkedCatVal,fromRow,limitRow){
                isTokenValid,errorState,isThisLast,message,response->
            isLoading = false
            isLastPage = isThisLast
            toggleBottomProgress(false)
            if(fromRow == 0){
                forum_list_progress_center?.visibility = View.GONE
                forum_list_rv?.visibility = View.VISIBLE
            }
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    showToast(message)
                }else{
                    response?.let {resp->
                        //do process here
                        if(fromRow == 0){
                            forumAdapter.clear()
                            if(checkedCatVal.isEmpty() && checkedSortVal.isEmpty()){
                                forum_list_title?.text = "ALL DISCUSSIONS"
                            }
                        }
                        if(resp.rows.isNotEmpty()){
                            forumAdapter.addAll(resp.rows.map { row->ItemKnowledgeForum(row,false,{rowCallback->
                                //showToast(rowCallback.name)
                                navigateToForumDetail(rowCallback.name,rowCallback.discussionid,checkedCatVal,checkedSortVal, rowCallback.can_edit,rowCallback.can_delete )
                            },{rowForum ->
                                MLLog.showLog(classTag,"do nothin")
                                })
                            })
                        }
                    }?:run {
                        showToast(message)
                    }
                }
            }
        }
    }

    private fun getForumFilter(){
        toggleProgress(true)
        genViewModel.getForumFilter{isTokenValid,errorState,message,response->
            toggleProgress(false)
            if(!isTokenValid){
                navigateTo<LoginActivity>(true)
            }else{
                if(errorState){
                    showToast(message)
                }else{
                    //do action here
                    response?.let {
                        setFilterAndSortRadio(it)
                    }?:run {
                        showToast(message)
                    }

                }
            }
        }
    }

    private fun toggleProgress(state:Boolean){
        progress_forum_sort?.visibility = if(state) View.VISIBLE else View.GONE
        progress_forum_cat?.visibility = if(state) View.VISIBLE else View.GONE
    }


    private fun toggleBottomProgress(state:Boolean){
        if(state){
            forum_list_bottom_progress?.visibility = View.VISIBLE
            forum_list_bottom_progress?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }else{
            forum_list_bottom_progress?.layoutParams?.height = 1
            forum_list_bottom_progress?.visibility = View.INVISIBLE
        }
        forum_list_bottom_progress?.requestLayout()
        forum_list_rv?.requestLayout()
    }



    private fun setFilterAndSortRadio(response:ResultForumFilter){
        /*val rbAll =  RadioButton(this)
        rbAll.id = View.generateViewId()
        rbAll.setOnCheckedChangeListener { _, _ ->
            checkedCatVal = ""
        }
        rbAll.text = "All Categories"
        forum_radio_button_categories_container?.addView(rbAll)*/
        for(cat in response.categories){
            val rb = RadioButton(this)
            rb.id = View.generateViewId()
            rb.setOnCheckedChangeListener { _, state ->
                /*checkedCatVal = if (state) {
                    cat.value
                }else ""*/
                when(state){
                    true->{
                        checkedCatVal = cat.value
                        checkedCatLabel = cat.label
                    }
                }

            }
            rb.text = cat.label
            forum_radio_button_categories_container?.addView(rb)
        }

        for(sortVal in response.sortby){
            val rb = RadioButton(this)
            rb.id = View.generateViewId()
            rb.setOnCheckedChangeListener { _, state ->
                when(state){
                    true->{
                        checkedSortVal = sortVal.value
                        checkedSortLabel = sortVal.label
                    }
                }
            }
            rb.text = sortVal.label
            forum_radio_button_sort_container?.addView(rb)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.END)) {
            drawer_layout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_knowledge_forum, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.action_settings -> return true
                else -> return super.onOptionsItemSelected(it)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
