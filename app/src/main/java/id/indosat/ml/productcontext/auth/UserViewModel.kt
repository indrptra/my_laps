package id.indosat.ml.productcontext.auth

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.request.MLLoginRequest
import id.indosat.ml.common.model.response.*
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.repository.IMLRepository
import id.indosat.ml.util.MLLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserViewModel @Inject constructor(val mlRepository:IMLRepository):ViewModel(){
    companion object {
        const val classTag = "USERVIEWMODEL"
    }
    var fromRowSorted = 0
    var limitRowSorted = MLConfig.LimitData
    var totalDataSorted = 0
    val compositeDisposable = CompositeDisposable()
    fun clear(){
        compositeDisposable.dispose()
    }
    inline fun doLogin(username:String,password:String,crossinline callback:(Boolean,String,ResultLogin?)->Unit){
        val model = MLLoginRequest(username,password)
        compositeDisposable.add(
            //mlRepository.doLogin(username,password)
            mlRepository.doPostLogin(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                    callback(response.error,response.message,response.result)
                    MLLog.showLog(classTag,response.message)
                },{error->
                    callback(true,error.localizedMessage ?: "",null)
                    MLLog.showLog(classTag,error.localizedMessage ?: "")
                })
        )

    }

    fun saveLoginResult(result:ResultLogin){
        MLPrefModel.isUserLoggedIn = true
        MLPrefModel.isUserConfirmed = result.confirmed
        MLPrefModel.isUserSuspended = result.suspended
        MLPrefModel.userIdEmployee = result.idnumber
        MLPrefModel.userFullName = result.fullname
        MLPrefModel.userDept = result.department
        MLPrefModel.userId = result.id
        MLPrefModel.userToken = result.token
        MLPrefModel.userInstitution = result.institution
        if(!result.address.isNullOrBlank()) MLPrefModel.userAddress = result.address else MLPrefModel.userAddress = ""
        if(!result.email.isNullOrBlank()) MLPrefModel.userEmail = result.email else MLPrefModel.userEmail = ""
        if(!result.profileimageurlsmall.isNullOrBlank())MLPrefModel.userImageSmall = result.profileimageurlsmall else MLPrefModel.userImageSmall = ""
        if(!result.email.isNullOrBlank()) MLPrefModel.userImageLarge = result.profileimageurl else MLPrefModel.userImageLarge = ""
    }


    inline fun getMyPoints(crossinline callback: (Boolean,Boolean, String, ResultMyPoint?) -> Unit){
        compositeDisposable.add(
            mlRepository.getMyPoints(MLPrefModel.userIdEmployee)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "",null)
                })
        )

    }

    inline fun getUserDashboard(crossinline callback: (Boolean, Boolean, String, ResultDashboard?) -> Unit){
        compositeDisposable.add(
            mlRepository.getUserDashboard(MLPrefModel.userToken,MLPrefModel.userIdEmployee)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "",null)
                })
        )

    }

    inline fun getLeaderBoard(crossinline callback: (Boolean, Boolean, String, ResultLeaderBoard?) -> Unit){
        compositeDisposable.add(
            mlRepository.getLeaderBoard(MLPrefModel.userToken,MLPrefModel.userIdEmployee)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    MLLog.showLog(classTag,error.localizedMessage ?: "No internet connection. Please refresh the page.")
                    callback(true,true,"No internet connection. Please refresh the page.",null)
                })
        )

    }

    inline fun getMyCourses(type:String,fromRow:Int=0,limitRow:Int=MLConfig.LimitData,
                     crossinline callback: (Boolean, Boolean,Boolean, String, ResultMyCourse?) -> Unit){
        compositeDisposable.add(
            mlRepository.getMyCourses(MLPrefModel.userToken,
                MLPrefModel.userId.toString(),
                type,fromRow,limitRow).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    totalDataSorted = result.result?.numfound ?: 0
                    fromRowSorted = (result.result?.endindex ?: 0)
                    fromRowSorted += 1
                    val isLastPage = (totalDataSorted - 1) == fromRowSorted
                    callback(result.validtoken,result.error,isLastPage,result.message,result.result)
                },{error->
                    callback(true,true,true,"No internet connection. Please refresh the page.",null)
                    MLLog.showLog(classTag,error.localizedMessage ?: "No internet connection. Please refresh the page.")
                })
        )
    }

    inline fun  getCoursesByType(type:String,page:Int=0,limit:Int=MLConfig.LimitData,
                                 crossinline callback: (Boolean, Boolean,Boolean, String, CourseResponse?) -> Unit) {
        compositeDisposable.add(
            mlRepository.getCoursesByType(MLPrefModel.userToken,
                MLPrefModel.userId.toString(), type, page, limit).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    totalDataSorted = result.paging?.total_rows ?: 0
                    //fromRowSorted =  result.paging?.last_page ?: 0
                    fromRowSorted += 1

                    if (result.paging?.last_page != null) totalDataSorted = result.paging?.last_page

                    var isLastPage = true

                    if (result.paging?.last_page != null) {
                        isLastPage = totalDataSorted == fromRowSorted
                    }

                    callback(result.validtoken, result.error!!, isLastPage, result.message!!, result)
                },{error->
                    callback(true,true,true,"No internet connection. Please refresh the page.",null)
                    MLLog.showLog(classTag,error.localizedMessage ?: "No internet connection. Please refresh the page.")
                })
        )
    }
}