package id.indosat.ml.viewmodel

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.ResultAutoCompleteSearch
import id.indosat.ml.common.model.response.ResultForum
import id.indosat.ml.common.model.response.ResultForumAutoComplete
import id.indosat.ml.common.model.response.ResultSearch
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.repository.IMLRepository
import id.indosat.ml.util.MLLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MLSearchViewModel @Inject constructor(val repo:IMLRepository):ViewModel(){
    companion object {
        const val classTag = "MLSearchViewModel"
    }
    var fromRowSorted = 0
    var limitRowSorted = MLConfig.LimitData
    var totalDataSorted = 0
    var keyword = ""
    val compositeDisposable = CompositeDisposable()
    fun clear(){
        compositeDisposable.dispose()
    }
    inline fun getAutoSearchComplete(_keyword:String,crossinline callback:(Boolean,Boolean,String,ResultAutoCompleteSearch?)->Unit){
        compositeDisposable.add(
        repo.getAutoCompleteSearch(MLPrefModel.userToken,_keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                callback(result.validtoken,result.error,result.message,result.result)
                //MLLog.showLog(classTag,result?.message ?: "")
            },{error->
                //MLLog.showLog(classTag,error.localizedMessage ?: "")
                callback(true,true,error.localizedMessage ?: "",null)
            })
        )
    }

    inline fun getForumAutoCompleteSearch(keyword:String,crossinline callback: (Boolean, Boolean, String, ResultForumAutoComplete?) -> Unit){
        compositeDisposable.add(
        repo.getForumAutoCompleteSearch(keyword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({result->
                callback(result.validtoken,result.error,result.message,result.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "",null)
            })
        )
    }

    inline fun getSearchResult(_keyword: String,fromRow:Int=0,limitRow:Int=limitRowSorted,
                               crossinline callback: (Boolean, Boolean,Boolean, String, ResultSearch?) -> Unit){
        keyword = _keyword
        compositeDisposable.add(
        repo.getSearchResult(MLPrefModel.userToken,keyword,fromRow,limitRow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                totalDataSorted = response.result?.numfound ?: 0
                fromRowSorted = response.result?.endindex ?: 0
                fromRowSorted += 1
                val isLastPage = (totalDataSorted - 1) == fromRowSorted
                callback(response.validtoken,response.error,isLastPage,response.message,response.result)
            },{error->
                callback(true,true,true,error.localizedMessage ?: "",null)
            })
        )
    }


    inline fun getSearchForum(keyword:String,fromRow:Int=0,limitRow:Int=MLConfig.LimitData,
                                 crossinline callback:(Boolean, Boolean, Boolean, String, ResultForum?)->Unit){
        compositeDisposable.add(
            repo.getForumSearch(keyword,MLPrefModel.userToken,fromRow,limitRow)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    result?.let {response->
                        totalDataSorted = response.result?.numfound ?: 0
                        fromRowSorted = (response.result?.endindex ?: 0)
                        fromRowSorted += 1
                        val isLastPage = (totalDataSorted - 1) == fromRowSorted
                        callback(response.validtoken,response.error,isLastPage,response.message,response.result)
                    }?:run {
                        callback(true,false,true,"No internet connection. Please refresh the page.",null)
                    }
                },{error->
                    callback(true,false,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                    MLLog.showLog("MLForumViewModel",error.localizedMessage ?: "No internet connection. Please refresh the page.")
                })
        )
    }
}