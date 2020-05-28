package id.indosat.ml.viewmodel

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.request.MLPostCommentRequest
import id.indosat.ml.common.model.request.MLPostRatingCourseRequest
import id.indosat.ml.common.model.response.*
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.repository.IMLRepository
import id.indosat.ml.util.MLLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class MLCourseViewModel @Inject constructor(val repo:IMLRepository):ViewModel(){
    var selectedCat = -99
    var fromRowSorted = 0
    var limitRowSorted = MLConfig.LimitData
    var totalDataSorted = 0

    var fromRowComment = 0
    var limitRowComment = MLConfig.LimitData
    var totalDataComment = 0
    val compositeDisposable = CompositeDisposable()
    fun clear(){
        compositeDisposable.dispose()
    }
    inline fun getSortedCourse(type:String,crossinline callback:(Boolean,Boolean,String,ResultSortedCourse?)->Unit){
        compositeDisposable.add(
        repo.getSortedCourses(MLPrefModel.userToken,type.toLowerCase())
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({result->
                result?.let {response->

                    callback(response.validtoken,response.error,response.message,response.result)
                }?:run {
                    callback(true,false,"No internet connection. Please refresh the page.",null)
                }
            },{error->
                callback(true,false,"No internet connection. Please refresh the page.",null)
                MLLog.showLog("COURSE VIEWMODEL",error.localizedMessage ?: "")
            })
        )
    }

    inline fun getSortedCourseLimit(type:String,fromRow:Int=0,limitRow:Int=limitRowSorted,crossinline callback:(Boolean,Boolean,Boolean,String,ResultSortedCourse?)->Unit){
        compositeDisposable.add(
        repo.getSortedCoursesLimit(MLPrefModel.userToken,type.toLowerCase(),fromRow,limitRow)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({result->
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
                callback(true,false,true,"No internet connection. Please refresh the page.",null)
                MLLog.showLog("COURSE VIEWMODEL",error.localizedMessage ?: "")
            })
        )
    }



    inline fun getCategoryCourse(fromRow:Int=0,limitRow:Int=limitRowSorted,crossinline callback: (Boolean, Boolean,Boolean, String, ResultCoursePerCat?) -> Unit){
        compositeDisposable.add(
        repo.getCoursePerCat(MLPrefModel.userToken,selectedCat,fromRow,limitRow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                totalDataSorted = response.result?.numfound ?: 0
                fromRowSorted = (response.result?.endindex ?: 0)
                fromRowSorted += 1
                val isLastPage = (totalDataSorted - 1) == fromRowSorted
                callback(response.validtoken,response.error,isLastPage,response.message,response.result)
                MLLog.showLog("COURSE VIEW MODEL RESPONSE",response?.toString() ?: "")
            },{error->
                MLLog.showLog("COURSE VIEW MODEL",error.localizedMessage ?: "")
                callback(true,true,true,error.localizedMessage ?: "",null)
            })
        )
    }

    inline fun getCourseSubCategories(catId:Int,crossinline callback: (Boolean, Boolean, String, ResultSubCategories?) -> Unit){
        compositeDisposable.add(
        repo.getCourseSubCategories(MLPrefModel.userToken,catId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                callback(result.validtoken,result.error,result.message,result.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "",null)
                MLLog.showLog("COURSE VIEW MODEL",error.localizedMessage ?: "")
            })
        )
    }

    inline fun getCourseDetail(courseId:Int,crossinline callback: (Boolean, Boolean, String, ResultCourseDetail?) -> Unit){
        compositeDisposable.add(
        repo.getCourseDetail(MLPrefModel.userToken,courseId,1,MLPrefModel.userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                callback(result.validtoken,result.error,result.message,result.result)
            },{error->
                callback(true,true,error.localizedMessage?:"No internet connection. Please refresh the page.",null)
            })
        )
    }

    inline fun getCourseDetailComments(fromRow: Int=0,
                                limitRow: Int=MLConfig.LimitData,
                                courseId: Int,
                                crossinline callback: (Boolean, Boolean, Boolean, String, ResultComment?) -> Unit){
        compositeDisposable.add(
        repo.getComments(MLPrefModel.userToken,courseId,fromRow,limitRow)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                totalDataComment = response.result?.numfound ?: 0
                fromRowComment = (response.result?.endindex ?: 0)
                fromRowComment += 1
                //val isLastPage = (totalDataSorted - 1) == fromRowSorted
                val isLastPage = (totalDataComment - 1) == fromRowComment
                callback(response.validtoken,response.error,isLastPage,response.message,response.result)
                MLLog.showLog("COURSE VIEW MODEL RESPONSE",response?.toString() ?: "")
            },{error->
                MLLog.showLog("COURSE VIEWMODEL GET COMMENTS",error.localizedMessage ?: "")
                callback(true,true,true,error.localizedMessage ?: "",null)
            })
        )
    }


    inline fun getCourseScormURL(courseId:Int,crossinline callback: (Boolean, Boolean, String, String?) -> Unit){
        compositeDisposable.add(
        repo.getScormURL(MLPrefModel.userToken,courseId,MLPrefModel.userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({result->
                result.result?.let {
                    callback(result.validtoken,result.error,result.message,it.url)
                }
            },{error->
                MLLog.showLog("COURSE VIEW MODEL GET SCORMURL",error.localizedMessage ?: "")
                callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
            })
        )
    }

    inline fun postComment(comment:String,courseId: Int,
                           crossinline callback: (Boolean, Boolean, String, ResultPostComment?) -> Unit){
        val postModel = MLPostCommentRequest(comment,courseId,MLPrefModel.userToken,MLPrefModel.userId)
        compositeDisposable.add(
        repo.postComment(postModel)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response->
                callback(response.validtoken,response.error,response.message,response.result)
            },{error->
                MLLog.showLog("COURSE VIEW MODE POST COMMENT",error.localizedMessage ?: "No internet connection. Please refresh the page.")
                callback(true,true,"No internet connection. Please refresh the page.",null)
            })
        )
    }


    inline fun postRating(courseId: Int,rating:Int,crossinline callback: (Boolean, Boolean, String, ResultPostRating?) -> Unit){
        val model = MLPostRatingCourseRequest(courseId,rating,MLPrefModel.userToken,MLPrefModel.userId)
        compositeDisposable.add(
            repo.postRatingCourse(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    MLLog.showLog("COURSE VIEWMODEL POST RATING",error.localizedMessage ?: "No internet connection. Please refresh the page.")
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }


    inline fun getRating(courseId: Int,crossinline callback: (Boolean, Boolean, String, ResultGetCourseRating?) -> Unit){
        compositeDisposable.add(
            repo.getCourseRating(courseId,MLPrefModel.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun setEnrollOrComplete(courseId: Int,moduleId:Int,
                            crossinline callback: (Boolean, Boolean, String, ResultModuleNS?) -> Unit){
        compositeDisposable.add(
            repo.setEnrolCompleteModuleNS(courseId,moduleId,MLPrefModel.userToken,MLPrefModel.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun setEnrollOrCompleteNew(courseId: Int,moduleId:Int,
                            crossinline callback: (Boolean, Boolean, String, ResultModuleNS?) -> Unit){
        compositeDisposable.add(
            repo.setEnrolCompleteModuleNSNew(courseId,moduleId,MLPrefModel.userToken,MLPrefModel.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.validtoken,response.error,response.message,response.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun startEnroll(courseId: Int, crossinline callback: (Boolean, String, MLUserEnrollResult?) -> Unit){
        compositeDisposable.add(
            repo.startCourse(courseId,MLPrefModel.userId,MLPrefModel.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.error,response.message,response.result)
                },{error->
                    callback(true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun setPoint(courseId: Int, moduleId: Int, action: String,
                        crossinline callback: (Boolean, String, SetPointResult?) -> Unit){
        compositeDisposable.add(
            repo.setPoint(courseId, moduleId,MLPrefModel.userToken, MLPrefModel.userId, action)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({response->
                    callback(response.error,response.message,response.result)
                },{error->
                    callback(true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

}