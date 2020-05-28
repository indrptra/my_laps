package id.indosat.ml.viewmodel

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.request.MLRegisterDeviceIdRequest
import id.indosat.ml.common.model.response.*
import id.indosat.ml.core.repository.IMLRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import javax.inject.Inject

class MLGeneralViewModel @Inject constructor(val repo: IMLRepository): ViewModel(){
    val compositeDisposable = CompositeDisposable()
    fun clear(){
        compositeDisposable.dispose()
    }
    inline fun getMainMenu(crossinline callback:(Boolean,Boolean,String,ResultMainMenu?)->Unit){
        compositeDisposable.add(
        repo.getMainMenu(MLPrefModel.userToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                response?.let {resp->
                    callback(resp.validtoken,resp.error,resp.message,resp.result)
                }?:run{
                    callback(true,true,"No internet connection. Please refresh the page.",null)
                }
            },{error->
                if(error is IOException){
                    callback(true,false,error.localizedMessage ?: "",null)
                }else{
                    callback(false,false,error.localizedMessage ?: "",null)
                }

            })
        )
    }

    inline fun getHomeSlider(crossinline callback: (Boolean, Boolean, String, ResultHomeSlider?) -> Unit){
        compositeDisposable.add(
        repo.getHomeSlider()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                response?.let {resp->
                    callback(true,resp.error,resp.message,resp.result)
                }?:run{
                    callback(true,true,"No internet connection. Please refresh the page.",null)
                }
            },{error->
                if(error is IOException){
                    callback(true,false,error.localizedMessage ?: "",null)
                }else{
                    callback(false,false,error.localizedMessage ?: "",null)
                }
            })
        )
    }

    inline fun getOnBoarding(crossinline callback: (Boolean, Boolean, String, ResultOnBoarding?) -> Unit){
        compositeDisposable.add(
        repo.getOnBoarding()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response->
                callback(response.validtoken,response.error,response.message,response.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "",null)
            })
        )
    }

    inline fun getURLSupport(crossinline callback: (Boolean, Boolean, String, ResultSupportURL?) -> Unit){
        compositeDisposable.add(
        repo.getURLSupport()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                callback(response.validtoken,response.error,response.message,response.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page." ,null)
            })
        )
    }

    inline fun getForumFilter(crossinline callback: (Boolean, Boolean, String, ResultForumFilter?) -> Unit){
        compositeDisposable.add(
        repo.getForumFilter(MLPrefModel.userToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                callback(result.validtoken,result.error,result.message,result.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
            })
        )
    }
    
    inline fun registerDeviveId(deviceid: String, crossinline callback: (Boolean, Boolean, String, ResultRegisterDeviceId?) -> Unit){
        compositeDisposable.add(
            repo.registerDeviceId(MLRegisterDeviceIdRequest(MLPrefModel.userToken, deviceid))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun getStaticSlider(crossinline callback: (Boolean, Boolean, String, List<ResultStaticSlider>?) -> Unit){
        compositeDisposable.add(
            repo.getStaticSlider()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                    response?.let {resp->
                        callback(true,resp.error,resp.message,resp.result)
                    }?:run{
                        callback(true,true,"No internet connection. Please refresh the page.",null)
                    }
                },{error->
                    if(error is IOException){
                        callback(true,false,error.localizedMessage ?: "",null)
                    }else{
                        callback(false,false,error.localizedMessage ?: "",null)
                    }
                })
        )
    }
}