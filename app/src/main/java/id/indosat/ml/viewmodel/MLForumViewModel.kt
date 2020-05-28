package id.indosat.ml.viewmodel

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.ml_enum.MLESortStringType
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.request.*
import id.indosat.ml.common.model.response.*
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.repository.IMLRepository
import id.indosat.ml.util.MLLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MLForumViewModel @Inject constructor(val repo:IMLRepository):ViewModel(){
    var fromRowForum = 0
    var limitRowForum = MLConfig.LimitData
    var totalDataForum = 0


    var fromRowForumComment = 0
    var limitRowForumComment = MLConfig.LimitData
    var totalDataForumComment = 0

    val compositeDisposable = CompositeDisposable()
    fun clear(){
        compositeDisposable.dispose()
    }
    inline fun resetDataCounter(){
        fromRowForum = 0
        limitRowForum = MLConfig.LimitData
        totalDataForum = 0
    }

    inline fun resetDataCounterComment(){
        fromRowForumComment = 0
        limitRowForumComment = MLConfig.LimitData
        totalDataForumComment = 0
    }

    inline fun getKnowledgeForum(sortBy:String,category:String,fromRow:Int=0,limitRow:Int=MLConfig.LimitData,
                          crossinline callback:(Boolean,Boolean,Boolean,String,ResultForum?)->Unit){
        compositeDisposable.add(
        repo.getKnowledgeForum(MLPrefModel.userToken,sortBy,category,fromRow,limitRow)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                result?.let {response->
                    totalDataForum = response.result?.numfound ?: 0
                    fromRowForum = (response.result?.endindex ?: 0)
                    fromRowForum += 1
                    val isLastPage = (totalDataForum - 1) == fromRowForum
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


    inline fun getMyDiscussion(sortBy:String,category:String,fromRow:Int=0,limitRow:Int=MLConfig.LimitData,
                                 crossinline callback:(Boolean,Boolean,Boolean,String,ResultForum?)->Unit){
        compositeDisposable.add(
            repo.getMyDiscussion(MLPrefModel.userId,MLPrefModel.userToken,sortBy,category,fromRow,limitRow)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    result?.let {response->
                        totalDataForum = response.result?.numfound ?: 0
                        fromRowForum = (response.result?.endindex ?: 0)
                        fromRowForum += 1
                        val isLastPage = (totalDataForum - 1) == fromRowForum
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

    inline fun getDetailForum(discussionId:Int,sortBy: String,category: String,crossinline callback: (Boolean, Boolean, String, ResultDFR?) -> Unit){
        compositeDisposable.add(
        repo.getDetailForum(MLPrefModel.userToken,discussionId,sortBy,category)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                callback(response.validtoken,response.error,response.message,response.result)
            },{error->
                callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
            })
        )
    }

    inline fun getForumComments(discussionId: Int,sortType:String = MLESortStringType.ASCENDING_DATE.type,
                         fromRow: Int = fromRowForumComment,
                         limitRow: Int = limitRowForumComment,
                                crossinline callback: (Boolean, Boolean,Boolean, String, ResultForumComment?) -> Unit ){
        compositeDisposable.add(
            repo.getForumComments(discussionId,MLPrefModel.userToken,sortType,fromRow,limitRow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({response->
                    totalDataForumComment = response.result?.numfound ?: 0
                    fromRowForumComment = (response.result?.endindex ?: 0)
                    fromRowForumComment += 1
                    val isLastPage = (totalDataForumComment - 1) == fromRowForumComment
                    callback(response.validtoken,response.error,isLastPage,response.message,response.result)
                },{error->
                    callback(true,true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )

    }

    inline fun postSimpleComment(postId:Int,subject:String,message:String,
                          crossinline callback: (Boolean, Boolean, String, ResultSimpleForumComment?) -> Unit ){
        compositeDisposable.add(
            repo.postSimpleForumComment(MLSImpleForumCommentRequest(message,postId,subject,MLPrefModel.userToken))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }


    inline fun getSubscribedStatus(discussionId: Int,
                            crossinline callback: (Boolean, Boolean, String, ResultForumCheckSubs?) -> Unit){
        compositeDisposable.add(
            repo.getForumIsSubscribed(MLPrefModel.userToken,discussionId,MLPrefModel.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }


    inline fun getLikeStatus(discussionId: Int,postId: Int,
                      crossinline callback: (Boolean, Boolean, String, ResultForumCheckLike?) -> Unit){
        compositeDisposable.add(
            repo.getForumLikeStatus(MLPrefModel.userToken,discussionId,postId,MLPrefModel.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result ->
                    callback(result.validtoken,result.error,result.message,result.result)
                } ,{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun postSubscribeForum(discussionId: Int,
                                  crossinline callback: (Boolean, Boolean, String, ResultSubsUnsubsForum?)-> Unit){
        val model = MLSubsUnsubsForumRequest(discussionId,MLPrefModel.userToken,MLPrefModel.userId)
        compositeDisposable.add(
            repo.postForumSubscribe(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun postUnSubscribeForum(discussionId: Int,
                                  crossinline callback: (Boolean, Boolean, String, ResultSubsUnsubsForum?)-> Unit){
        val model = MLSubsUnsubsForumRequest(discussionId,MLPrefModel.userToken,MLPrefModel.userId)
        compositeDisposable.add(
            repo.postForumUnsubscribe(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun sendLikeForum(discussionId: Int,postId: Int, like: Int,
                      crossinline callback: (Boolean, Boolean, String, ResultSendLikeForum?) -> Unit){
        compositeDisposable.add(
            repo.sendLikeForum(MLPrefModel.userToken,MLPrefModel.userId,discussionId,postId,like)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun getDiscussionCategories(crossinline callback: (Boolean, Boolean, String, List<String>?) -> Unit){
        compositeDisposable.add(
            repo.getDiscussionCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun postCreateDiscussion(category: String,title:String,content:String,
                             crossinline callback: (Boolean, Boolean, String, ResultCreateDiscussion?)->Unit){
        val model = MLCreateDiscussionRequest(category,content,title,MLPrefModel.userToken)
        compositeDisposable.add(
            repo.postCreateDiscussion(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun postEditDiscussion(discussionId: String,category: String,title:String,content:String,
                             crossinline callback: (Boolean, Boolean, String, ResultCreateDiscussion?)->Unit){
        val model = MLEditDiscussionRequest(category,content,title,MLPrefModel.userToken, MLPrefModel.userId.toString())
        compositeDisposable.add(
            repo.postEditDiscussion(discussionId, MLPrefModel.userToken, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun deleteDiscussion(discussionId: String,
                             crossinline callback: (Boolean, Boolean, String, ResultCreateDiscussion?)->Unit){
//        val model = MLCreateDiscussionRequest(category,content,title,MLPrefModel.userToken)
        compositeDisposable.add(
            repo.deleteDiscussion(discussionId, MLPrefModel.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun updatePost(postId: String,category: String,title:String,content:String,
                             crossinline callback: (Boolean, Boolean, String, ResultCreateDiscussion?)->Unit){
        val model = MLEditCommentRequest(content,title)
        compositeDisposable.add(
            repo.updatePost(postId, MLPrefModel.userToken,model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun deletePost(postId: String,
                             crossinline callback: (Boolean, Boolean, String, ResultCreateDiscussion?)->Unit){
//        val model = MLCreateDiscussionRequest(category,content,title,MLPrefModel.userToken)
        compositeDisposable.add(
            repo.deletePost(postId, MLPrefModel.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline fun postCloseDiscussion(discussionId: Int,
                            crossinline callback: (Boolean, Boolean, String, ResultCloseDiscussion?)->Unit){
        val model = MLCloseDiscussionRequest(discussionId,MLPrefModel.userToken,MLPrefModel.userId)
        compositeDisposable.add(
            repo.closeMyDiscussion(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    callback(result.validtoken,result.error,result.message,result.result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )
    }

    inline  fun reportForum(postId: Int, discussionId: Int, category: String, reason: String,
                            crossinline  callback: (Boolean, Boolean,  String, MLReportDiscussionResponse?) -> Unit) {

        val model = MLReportDiscussionRequest(category, reason)

        compositeDisposable.add(
            repo.reportForum(model, postId, discussionId,MLPrefModel.userToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({result->
                    callback(result.validtoken, result.error, result.message, result)
                },{error->
                    callback(true,true,error.localizedMessage ?: "No internet connection. Please refresh the page.",null)
                })
        )

    }

}