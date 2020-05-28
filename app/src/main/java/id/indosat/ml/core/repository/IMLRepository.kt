package id.indosat.ml.core.repository

import id.indosat.ml.common.model.request.*
import id.indosat.ml.common.model.response.*
import io.reactivex.Flowable
import io.reactivex.Single

interface IMLRepository{
    fun getServerTime():Single<MLServerTimeResponse>

    fun getOnBoarding():Single<MLOnboardingResponse>

    fun doLogin(username:String,password:String):Single<MLLoginResponse>

    fun doPostLogin(model:MLLoginRequest):Single<MLLoginResponse>

    fun getMainMenu(token:String):Single<MLMainMenuResponse>

    fun getSortedCourses(token: String,type:String):Single<MLSortedCoursesResponse>

    fun getSortedCoursesLimit(token:String,type:String,fromRow:Int,limitRow:Int):Flowable<MLSortedCoursesResponse>

    fun getHomeSlider():Single<MLHomeSliderResponse>

    fun getAutoCompleteSearch(token:String,keyword:String):Single<MLAutoCompleteSearchResponse>

    fun getSearchResult(token: String,keyword: String,fromRow: Int,limitRow: Int):Flowable<MLSearchResponse>

    fun getCoursePerCat(token: String,category:Int,fromRow: Int,limitRow: Int):Flowable<MLCoursePerCatResponse>

    fun getCourseSubCategories(token: String,category: Int):Single<MLCourseSubCategoriesResponse>

    fun getMyPoints(username: String):Single<MLMyPointsResponse>

    fun getUserDashboard(token: String,username: String):Single<MLDashboardResponse>

    fun getLeaderBoard(token: String,username: String):Single<MLLeaderBoardResponse>

    fun getCourseDetail(token: String,id:Int,showModules:Int,user:Int):Single<MLCourseDetailResponse>

    fun getURLSupport():Single<MLSupportURLResponse>

    fun getMyCourses(token: String,userid:String,type:String,fromRow: Int,limitRow: Int):Flowable<MLMyCourseResponse>

    fun getCoursesByType(token: String,userid:String,type:String,page:Int, limit:Int): Flowable<CourseResponse>

    fun getMyCoursesCompleted(token: String,user:String):Flowable<MLMyCourseCompletedResponse>

    fun getComments(token: String,course:Int,fromRow: Int,limitRow: Int):Flowable<MLGetCommentsResponse>

    fun postComment(postModel:MLPostCommentRequest):Single<MLPostCommentResponse>

    fun getForumFilter(token: String):Single<MLForumFilterResponse>

    fun getKnowledgeForum(token: String,sortBy:String,category: String,fromRow:Int,limitRow: Int):Flowable<MLForumResponse>

    fun getDetailForum(token: String,discussionId:Int,sortBy:String,category: String):Single<MLDetailForumResponse>

    fun getScormURL(token: String,courseId: Int,userId:Int):Single<MLScormUrlReqResponse>

    fun getForumAutoCompleteSearch(keyword: String):Flowable<MLForumAutoCompleteResponse>

    fun getForumSearch(keyword: String,token: String,fromRow: Int,limitRow: Int):Flowable<MLForumResponse>

    fun postRatingCourse(model:MLPostRatingCourseRequest):Single<MLPostRatingCourseResponse>

    fun getForumIsSubscribed(token:String,discussionId: Int,userId: Int):Single<MLForumCheckSubscribedResponse>

    fun postForumSubscribe(model:MLSubsUnsubsForumRequest):Single<MLSubsUnsubsForumResponse>

    fun postForumUnsubscribe(model:MLSubsUnsubsForumRequest):Single<MLSubsUnsubsForumResponse>

    fun getForumComments(discussionId: Int,token: String,sortType:String,fromRow: Int,limitRow: Int) : Flowable<MLForumListCommentsResponse>

    fun getCourseRating(courseId: Int,token: String) : Single<MLGetCourseRatingResponse>

    fun getMyDiscussion(userId: Int,
                        token: String,
                        sortBy: String,
                        category: String,
                        fromRow: Int,limitRow: Int):Flowable<MLForumResponse>

    fun postSimpleForumComment(model:MLSImpleForumCommentRequest) : Single<MLSimpleForumCommentResponse>

    fun getForumLikeStatus(token: String,discussionId: Int,postId:Int,userId: Int):Single<MLForumCheckLikeResponse>

    fun getDiscussionCategories():Single<MLDiscussionCategoriesResponse>

    fun postCreateDiscussion(model:MLCreateDiscussionRequest):Single<MLCreateDiscussionResponse>

    fun postEditDiscussion(discussionId: String, token: String,model:MLEditDiscussionRequest):Single<MLCreateDiscussionResponse>

    fun deleteDiscussion(discussionId: String, token: String):Single<MLCreateDiscussionResponse>

    fun updatePost(postId: String, token: String, model:MLEditCommentRequest):Single<MLCreateDiscussionResponse>

    fun deletePost(postId: String, token: String):Single<MLCreateDiscussionResponse>

    fun sendLikeForum(token: String,userId: Int,discussionId: Int,postId: Int,like:Int):Single<MLSendLikeForumResponse>

    fun closeMyDiscussion(model:MLCloseDiscussionRequest):Single<MLCloseDiscussionResponse>

    fun setEnrolCompleteModuleNS(courseId: Int,moduleId:Int,token: String,userId: Int):Single<MLCourseModuleCompletionNonScormResponse>

    fun getNotifications(token: String,userId: Int,fromRow: Int,limitRow: Int):Flowable<MLNotificationResponse>

    fun markNotificationAsRead(id: Int,token: String,userId: Int):Single<MLNotificationReadResponse>

    fun setEnrolCompleteModuleNSNew(courseId: Int,moduleId:Int,token: String,userId: Int):Single<MLCourseModuleCompletionNonScormResponse>

    fun startCourse(courseId: Int,userEnroll:Int,token: String):Single<MLUserEnrollResponse>

    fun reportForum(request: MLReportDiscussionRequest, postid: Int,discussionid:Int,token: String):Single<MLReportDiscussionResponse>

    fun setPoint(courseId: Int, moduleId: Int, token: String, userId: Int, action: String):Single<MLSetPointResponse>

    fun registerDeviceId(request: MLRegisterDeviceIdRequest):Single<MLRegisterDeviceIdResponse>

    fun getStaticSlider():Flowable<MLStaticSliderResponse>
}