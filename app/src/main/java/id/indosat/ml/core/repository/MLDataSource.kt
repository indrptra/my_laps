package id.indosat.ml.core.repository

import id.indosat.ml.common.model.request.*
import id.indosat.ml.core.network.IMLNetworkService
import javax.inject.Inject

class MLDataSource @Inject constructor(private val netService:IMLNetworkService){
    fun getServerTime() = netService.getServerTime()

    fun getOnBoarding() = netService.getOnBoarding()

    fun doLogin(username:String,password:String) =
        netService.login(username,password)

    fun doPostLogin(model:MLLoginRequest) =
        netService.postLogin(model)

    fun getMainMenu(token:String) = netService.getMainMenu(token)

    fun getSortedCourses(token: String,type:String) = netService.getSortedCourses(token,type)

    fun getSortedCoursesLimit(token: String,type:String,fromRow:Int,limitRow:Int) =
        netService.getSortedCoursesLimit(token,type,fromRow,limitRow)

    fun getHomeSlider() = netService.getHomeSlider()

    fun getAutoCompleteSearch(token: String,keyword:String) = netService.getAutoCompleteSearch(token,keyword)

    fun getSearchResult(token: String,keyword: String,fromRow: Int,limitRow: Int) =
        netService.getSearchResult(token,keyword,fromRow,limitRow)

    fun getCoursePerCat(token: String,category:Int,fromRow: Int,limitRow: Int) =
        netService.getCoursePerCat(token,category,fromRow,limitRow)

    fun getMyPoints(userName:String) = netService.getMyPoints(userName)

    fun getUserDashboard(token: String,username: String) = netService.getUserDashboard(token,username)

    fun getLeaderBoard(token: String,username: String) = netService.getLeaderBoard(token,username)

    fun getCourseDetail(token:String,id:Int,showModules:Int=1,user:Int) =
        netService.getCourseDetail(token,id,showModules,user)

    fun getURLSupport() = netService.getURLSupport()

    fun getCoursesByType(token: String,userid:String,type:String,page:Int,limit: Int) =
        netService.getCoursesByType(token,userid,type,page,limit)

    fun getMyCourse(token: String,userid:String,type:String,fromRow:Int,limitRow: Int) =
            netService.getMyCourses(token,userid,type,fromRow,limitRow)

    fun getMyCourseCompleted(token: String,userid:String) =
            netService.getMyCoursesCompleted(token,userid)

    fun getCourseSubCategories(token: String,catId:Int) = netService.getCourseSubCategories(token,catId)

    fun getCommentsResponse(token: String,courseId:Int,fromRow: Int,limitRow: Int) =
        netService.getCourseComments(token,courseId,fromRow,limitRow)

    fun postComment(postModel:MLPostCommentRequest) = netService.postCourseComment(postModel)

    fun getForumFilter(token: String) = netService.getForumFilter(token)

    fun getKnowledgeForum(token: String,sortBy:String,category:String,fromRow:Int,limitRow: Int) =
            netService.getKnowledgeForum(token,sortBy,category,fromRow,limitRow)

    fun getDetailForum(token: String,discussionId:Int,sortBy:String,category:String) =
            netService.getDetailForum(token,discussionId,sortBy,category)

    fun getScormURL(token: String,courseId: Int,userId:Int) = netService.getScormURLReq(token,courseId,userId)

    fun getForumAutoCompleteKeyword(keyword: String) = netService.getForumSearchAutoComplete(keyword)

    fun getForumSearch(keyword:String,token: String,fromRow: Int,limitRow: Int) =
        netService.getForumSearch(keyword,token,fromRow,limitRow)


    fun postRatingCourse(model:MLPostRatingCourseRequest) = netService.postRatingCourse(model)

    fun getForumIsSubscribed(token:String,discussionId: Int,userId:Int) =
        netService.getForumSubscribedStatus(token,discussionId,userId)

    fun postForumSubscribe(model:MLSubsUnsubsForumRequest) = netService.postSubsForum(model)

    fun postForumUnsubscribe(model:MLSubsUnsubsForumRequest) = netService.postUnSubsForum(model)

    fun getForumComments(discussionId: Int,token: String,
                         sortType:String,fromRow: Int,limitRow: Int) =
            netService.getForumComments(discussionId,token,sortType,fromRow,limitRow)

    fun getCourseRating(courseId: Int,token: String) = netService.getCourseRating(courseId,token)

    fun getMyDiscussion(userId: Int,
                        token: String,
                        sortBy: String,
                        category: String,
                        fromRow: Int,
                        limitRow: Int) = netService.getMyDiscussion(userId,token,sortBy,category,fromRow,limitRow)

    fun postSimpleForumComment(model:MLSImpleForumCommentRequest) = netService.postSimpleForumComment(model)

    fun getForumLikeStatus(token: String,discussionId: Int,postId:Int,userId: Int) =
        netService.getForumLikeStatus(token, discussionId, postId, userId)

    fun getDiscussionCategories() = netService.getDiscussionCategories()

    fun postCreateDiscussion(model:MLCreateDiscussionRequest) = netService.postCreateDiscussion(model)

    fun postEditDiscussion(discussionid:String, token: String, model:MLEditDiscussionRequest) = netService.postUpdateDiscussion(discussionid,token,model)

    fun deleteDiscussion(discussionid:String, token: String) = netService.deleteDiscussion(discussionid,token)

    fun updatePost(postid:String, token: String, model:MLEditCommentRequest) = netService.updatePost(postid,token, model)

    fun deletePost(postid:String, token: String) = netService.deletePost(postid,token)

    fun sendLikeForum(token: String,userId: Int,discussionId: Int,postId: Int, like: Int) =
        netService.sendLikedForum(token,userId,discussionId,postId, like)

    fun closeMyDiscussion(model:MLCloseDiscussionRequest) = netService.postCloseDiscussion(model)

    fun setEnrolCompletionModuleNS(courseId: Int,moduleId:Int,token: String,userId: Int) =
            netService.setEnrollCompleteModuleNS(courseId,moduleId,token, userId)

    fun getNotifications(token: String,userId: Int,fromRow: Int, limitRow: Int) = netService.getNotifications(token, userId, fromRow, limitRow)

    fun markNotificationAsRead(id: Int,token: String,userId: Int) = netService.markNotificationAsRead(id,token, userId)

    fun setEnrollCompleteModuleNSNew(courseId: Int,moduleId:Int,token: String,userId: Int) =
            netService.setEnrollCompleteModuleNSNew(courseId,moduleId,token, userId)

    fun startEnroll(courseId: Int,userEnroll:Int,token: String) =
        netService.startEnrollCourse(courseId, userEnroll, token)

    fun reportForum(request: MLReportDiscussionRequest, postid: Int,discussionid: Int,token: String) =
            netService.reportForum(request, postid, discussionid, token)

    fun setPoint(courseId: Int, moduleId: Int, token: String, userId: Int, action: String) =
            netService.setPoint(courseId, moduleId, token, userId, action)

    fun registerDeviceId(request: MLRegisterDeviceIdRequest) =
            netService.registerDeviceId(request)

    fun getStaticSlider() =
        netService.getStaticSlider()
}
