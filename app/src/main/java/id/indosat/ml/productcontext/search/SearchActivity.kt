package id.indosat.ml.productcontext.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.android.AndroidInjection
import id.indosat.ml.R
import id.indosat.ml.adaptercomponent.ItemSortedCourse
import id.indosat.ml.adaptercomponent.PaginationGLScrollListener
import id.indosat.ml.base.BaseActivity
import id.indosat.ml.common.coordinator.navigateTo
import id.indosat.ml.common.coordinator.navigateToCourseDetail
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.productcontext.auth.LoginActivity
import id.indosat.ml.util.MLLog
import id.indosat.ml.util.dpToPx
import id.indosat.ml.util.getDisplayMetrics
import id.indosat.ml.util.showToast
import id.indosat.ml.viewmodel.MLSearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.content_search.*

//import kotlinx.android.synthetic.main.notification_template_lines_media.view.*

class SearchActivity : BaseActivity() {

    lateinit var searchViewModel:MLSearchViewModel

    val autompAdapter = GroupAdapter<ViewHolder>()
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
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar_search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_search?.title = ""
        supportActionBar?.title = ""
        searchViewModel = ViewModelProviders.of(this,viewModelFactory).get(MLSearchViewModel::class.java)
        setListManager()
        setSearchView()
        search_view?.requestFocus()
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
        search_autocomplete_result?.layoutManager = autoCompLLM
        search_autocomplete_result?.adapter = autompAdapter

        val resultSearchLLM = GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
        search_result?.layoutManager =resultSearchLLM
        search_result?.adapter = resultAdapter
        search_result?.addOnScrollListener(object : PaginationGLScrollListener(resultSearchLLM){
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
        search_view?.setOnQueryTextFocusChangeListener { _, state ->
            if(state){
                requestKeyword(search_view?.query?.toString() ?: "")
                toggleViewSearch(true)

            }
        }
        search_view?.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let{
                    autompAdapter.clear()
                    if(it.isNotEmpty() && it.length >= 3 && !isSubmitted){
                        requestKeyword(it)
                    }
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                toolbar_search?.requestFocus()
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
        if(keyword.isEmpty()){empty_view.visibility = View.GONE;return}
        searchViewModel.getAutoSearchComplete(keyword){ isTokenValid,_,message,response->
            if(!isTokenValid){
                showToast(message)
                navigateTo<LoginActivity>(false)
            }else{
                response?.let {resp->
                    if (resp.numfound == 0) {
                        search_autocomplete_result.visibility = View.GONE
                        empty_view.visibility = View.VISIBLE
                    } else {
                        search_autocomplete_result.visibility = View.VISIBLE
                        empty_view.visibility = View.GONE
                    }
                    autompAdapter.addAll(resp.keywords.map { keyword -> ItemSearchAutoComplete(keyword){text->
                        isSubmitted = true
                        search_view?.setQuery(text,true)
                    }
                    })
                }
            }
        }
    }


    private fun toggleViewSearch(state:Boolean,fromRow: Int = 0){
        if(state){
            if(fromRow > 0){
                search_result?.visibility = View.VISIBLE
                search_progress_bottom?.visibility = View.INVISIBLE
                search_progress_bottom?.layoutParams?.height = 1
                search_progress_bottom?.requestLayout()
                search_result?.requestLayout()
            }else{
                search_result?.visibility = View.GONE
                search_progress?.visibility = View.GONE
                search_progress_bottom?.visibility = View.INVISIBLE
                search_autocomplete_result?.visibility = View.VISIBLE
            }
        }else{
            if(fromRow > 0){
                search_progress_bottom?.visibility = View.VISIBLE
                search_progress_bottom?.layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
                search_progress_bottom?.requestLayout()
                search_result?.requestLayout()
            }else{
                search_result?.visibility = View.GONE
                search_progress?.visibility = View.VISIBLE
                search_progress_bottom?.visibility = View.INVISIBLE
                search_progress_bottom?.layoutParams?.height = 1
                search_progress_bottom?.requestLayout()
                search_autocomplete_result?.visibility = View.GONE
            }
        }
    }


    private var isLastPage = false
    private var isLoading = false
    private fun doSearch(query:String,fromRow:Int=0,limitRow:Int=MLConfig.LimitData){
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
        empty_view.visibility = View.GONE
        toggleViewSearch(false,localFromRow)
        searchViewModel.getSearchResult(query,localFromRow,limitRow){isValidToken,_,isThisLast,message,response->
            isSubmitted = false
            isLoading = false
            isLastPage = isThisLast
            search_progress?.visibility = View.GONE
            search_result?.visibility = View.VISIBLE
            search_autocomplete_result?.visibility = View.GONE
            search_progress_bottom?.layoutParams?.height = 1
            search_progress_bottom?.visibility = View.INVISIBLE
            search_progress_bottom?.requestLayout()
            search_result?.requestLayout()
            if(!isValidToken){
                showToast(message)
                navigateTo<LoginActivity>(true)
            }else{
                response?.let {res->
                    if(localFromRow == 0){
                        resultAdapter.clear()
                    }

                    if (res.numfound == 0) {
                        empty_view.visibility = View.VISIBLE
                    } else {
                        empty_view.visibility = View.GONE
                    }

                    if(!res.rows.isEmpty()){
                        val eachWidthCard = (getDisplayMetrics().widthPixels - dpToPx(32)) / 2
                        resultAdapter.addAll(res.rows.map { ItemSortedCourse(it,eachWidthCard,dpToPx(MLConfig.CardHeight)){view, row->
                            //MLLog.showLog(classTag,row.fullname)
                            navigateToCourseDetail(row.id)
                        } })
                    }
                }
            }
        }
    }
}
