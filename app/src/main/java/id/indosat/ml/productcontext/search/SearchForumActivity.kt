package id.indosat.ml.productcontext.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemKnowledgeForum
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToForumDetail
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLSearchViewModel
import kotlinx.android.synthetic.main.activity_search_forum.*
import kotlinx.android.synthetic.main.content_search_forum.*

class SearchForumActivity : BaseActivity() {
    lateinit var searchViewModel:MLSearchViewModel
    private var checkedCatVal = ""
    private var checkedCatLabel = ""
    private var checkedSortVal = ""
    private var checkedSortLabel = ""
    val autoCompAdapter = GroupAdapter<ViewHolder>()
    val resultAdapter = GroupAdapter<ViewHolder>()
    override fun onDestroy() {
        super.onDestroy()
        searchViewModel.clear()
        try {
            generalViewModel.clear()
        }catch (e:Exception){
            MLLog.showLog(classTag,e.localizedMessage)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_forum)
        setSupportActionBar(sf_toolbar_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sf_toolbar_search?.title = ""
        supportActionBar?.title = ""
        searchViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLSearchViewModel::class.java)
        setListManager()
        setSearchView()
        sf_search_view?.requestFocus()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let{
            if(it.itemId == android.R.id.home){
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setListManager(){
        val autoCompLLM = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        sf_search_autocomplete_result?.layoutManager = autoCompLLM
        sf_search_autocomplete_result?.adapter = autoCompAdapter

        val resultSearchLLM = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        sf_search_result?.layoutManager =resultSearchLLM
        sf_search_result?.adapter = resultAdapter
        sf_search_result?.addOnScrollListener(object : PaginationGLScrollListener(resultSearchLLM){
            override fun loadMoreItems() {
                if(searchViewModel.totalDataSorted <= searchViewModel.fromRowSorted){return}
                if(searchViewModel.keyword.isEmpty()) {return}
                doSearch(searchViewModel.keyword,searchViewModel.fromRowSorted)
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }
    private var isSubmitted = false
    private fun setSearchView(){
        sf_search_view?.setOnQueryTextFocusChangeListener { _, state ->
            if(state){
                requestKeyword(sf_search_view?.query?.toString() ?: "")
                toggleViewSearch(true)

            }
        }
        sf_search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    autoCompAdapter.clear()
                    if(it.isNotEmpty() && it.length >= 3 && !isSubmitted){
                        requestKeyword(it)
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                sf_toolbar_search?.requestFocus()
                resultAdapter.clear()
                query?.let {
                    resultAdapter.clear()
                    doSearch(it)
                }
                return false
            }
        })
    }

    private fun requestKeyword(keyword:String){
        if(keyword.isEmpty()){return}
        searchViewModel.getForumAutoCompleteSearch(keyword){ isTokenValid,_,message,response->
            if(!isTokenValid){
                showToast(message)
                navigateTo<LoginActivity>(false)
            }else{
                response?.let {resp->
                    autoCompAdapter.addAll(resp.keywords.map { keyword -> ItemSearchAutoComplete(keyword){text->
                        isSubmitted = true
                        sf_search_view?.setQuery(text,true)
                    }
                    })
                }
            }
        }
    }


    private fun toggleViewSearch(state:Boolean,fromRow: Int = 0){
        if(state){
            if(fromRow > 0){
                sf_search_result?.visibility = View.VISIBLE
                sf_search_progress_bottom?.visibility = View.INVISIBLE
                sf_search_progress_bottom?.layoutParams?.height = 1
                sf_search_progress_bottom?.requestLayout()
                sf_search_result?.requestLayout()
            }else{
                sf_search_result?.visibility = View.GONE
                sf_search_progress?.visibility = View.GONE
                sf_search_progress_bottom?.visibility = View.INVISIBLE
                sf_search_autocomplete_result?.visibility = View.VISIBLE
            }
        }else{
            if(fromRow > 0){
                sf_search_progress_bottom?.visibility = View.VISIBLE
                sf_search_progress_bottom?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                sf_search_progress_bottom?.requestLayout()
                sf_search_result?.requestLayout()
            }else{
                sf_search_result?.visibility = View.GONE
                sf_search_progress?.visibility = View.VISIBLE
                sf_search_progress_bottom?.visibility = View.INVISIBLE
                sf_search_progress_bottom?.layoutParams?.height = 1
                sf_search_progress_bottom?.requestLayout()
                sf_search_autocomplete_result?.visibility = View.GONE
            }
        }
    }


    private var isLastPage = false
    private var isLoading = false
    private fun doSearch(query:String,fromRow:Int=0,limitRow:Int= MLConfig.LimitData){
        isSubmitted = true
        isLoading = true
        var localFromRow = fromRow
        if(query != searchViewModel.keyword){
            //need to reset All
            searchViewModel.fromRowSorted = 0
            searchViewModel.totalDataSorted = 0
            searchViewModel.limitRowSorted = MLConfig.LimitData
            localFromRow = 0
        }
        toggleViewSearch(false,localFromRow)
        searchViewModel.getSearchForum(query,localFromRow,limitRow){isValidToken,_,isThisLast,message,response->
            isSubmitted = false
            isLoading = false
            isLastPage = isThisLast
            sf_search_progress?.visibility = View.GONE
            sf_search_result?.visibility = View.VISIBLE
            sf_search_autocomplete_result?.visibility = View.GONE
            sf_search_progress_bottom?.layoutParams?.height = 1
            sf_search_progress_bottom?.visibility = View.INVISIBLE
            sf_search_progress_bottom?.requestLayout()
            sf_search_result?.requestLayout()
            if(!isValidToken){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                response?.let {res->
                    if(localFromRow == 0){
                        resultAdapter.clear()
                    }
                    if(!res.rows.isEmpty()){
                        /*val eachWidthCard = (getDisplayMetrics().widthPixels - dpToPx(32)) / 2
                        resultAdapter.addAll(res.rows.map { ItemSortedCourse(it,eachWidthCard,dpToPx(MLConfig.CardHeight)){ row->
                            //MLLog.showLog(classTag,row.fullname)
                            navigateToCourseDetail(row.id)
                        } })*/
                        resultAdapter.addAll(res.rows.map { row->
                            ItemKnowledgeForum(row,false,{ rowCallback->
                                //showToast(rowCallback.name)
                                navigateToForumDetail(rowCallback.name,rowCallback.discussionid,checkedCatVal,checkedSortVal,false,false)
                            },{
                                MLLog.showLog(classTag,"Do nothing")
                        }) })
                    }
                }
            }
        }
    }
}
