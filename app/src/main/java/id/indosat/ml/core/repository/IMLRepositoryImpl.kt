package id.indosat.ml.core.repository

import id.indosat.ml.common.model.request.*
import id.indosat.ml.common.model.response.*
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IMLRepositoryImpl @Inject constructor(private val dataSource:MLDataSource):IMLRepository{

    override fun setEnrolCompleteModuleNSNew(
        courseId: Int,
        moduleId: Int,
        token: String,
        userId: Int
    ): Single<MLCourseModuleCompletionNonScormResponse> {
        return dataSource.setEnrollCompleteModuleNSNew(courseId,moduleId,token,userId)
    }

    override fun getServerTime(): Single<MLServerTimeResponse> {
        return dataSource.getServerTime()
    }

    override fun getOnBoarding(): Single<MLOnboardingResponse> {
        return dataSource.getOnBoarding()
    }

    override fun doLogin(username: String, password: String): Single<MLLoginResponse> {
        return dataSource.doLogin(username,password)
    }

    override fun doPostLogin(model: MLLoginRequest): Single<MLLoginResponse> {
        return dataSource.doPostLogin(model)
    }

    override fun getMainMenu(token: String): Single<MLMainMenuResponse> {
        return dataSource.getMainMenu(token)
    }

    override fun getSortedCourses(token: String, type: String): Single<MLSortedCoursesResponse> {
        return dataSource.getSortedCourses(token,type)
    }

    override fun getSortedCoursesLimit(
        token: String,
        type: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLSortedCoursesResponse> {
        return dataSource.getSortedCoursesLimit(token,type,fromRow,limitRow)
    }

    override fun getHomeSlider(): Single<MLHomeSliderResponse> {
        return dataSource.getHomeSlider()
    }

    override fun getAutoCompleteSearch(token: String, keyword: String): Single<MLAutoCompleteSearchResponse> {
        return dataSource.getAutoCompleteSearch(token,keyword)
    }

    override fun getSearchResult(token: String, keyword: String,fromRow: Int,limitRow: Int): Flowable<MLSearchResponse> {
        return dataSource.getSearchResult(token,keyword,fromRow,limitRow)
    }

    override fun getCoursePerCat(token: String, category: Int,fromRow: Int,limitRow: Int): Flowable<MLCoursePerCatResponse> {
        return dataSource.getCoursePerCat(token,category,fromRow,limitRow)
    }

    override fun getCourseSubCategories(token: String, category: Int): Single<MLCourseSubCategoriesResponse> {
        return dataSource.getCourseSubCategories(token,category)
    }

    override fun getMyPoints(username: String): Single<MLMyPointsResponse> {
        return dataSource.getMyPoints(username)
    }

    override fun getUserDashboard(token: String, username: String): Single<MLDashboardResponse> {
        return dataSource.getUserDashboard(token,username)
    }

    override fun getLeaderBoard(token: String, username: String): Single<MLLeaderBoardResponse> {
        return dataSource.getLeaderBoard(token,username)
    }

    override fun getCourseDetail(token: String, id: Int,showModules:Int,user:Int): Single<MLCourseDetailResponse> {
        return dataSource.getCourseDetail(token,id,showModules,user)
    }

    override fun getScormURL(token: String, courseId: Int, userId: Int): Single<MLScormUrlReqResponse> {
        return dataSource.getScormURL(token,courseId,userId)
    }

    override fun getURLSupport(): Single<MLSupportURLResponse> {
        return dataSource.getURLSupport()
    }

    override fun getCoursesByType(
        token: String,
        userid: String,
        type: String,
        page: Int,
        limit: Int
    ): Flowable<CourseResponse> {
        return dataSource.getCoursesByType(token,userid,type,page,limit)
    }

    override fun getMyCourses(
        token: String,
        userid: String,
        type: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLMyCourseResponse> {
        return dataSource.getMyCourse(token,userid,type,fromRow,limitRow)
    }

    override fun getMyCoursesCompleted(
        token: String,
        user: String
    ): Flowable<MLMyCourseCompletedResponse> {
        return dataSource.getMyCourseCompleted(token,user)
    }


    override fun getComments(token: String, course: Int, fromRow: Int, limitRow: Int): Flowable<MLGetCommentsResponse> {
        return dataSource.getCommentsResponse(token,course,fromRow,limitRow)
    }

    override fun postComment(postModel: MLPostCommentRequest): Single<MLPostCommentResponse> {
        return dataSource.postComment(postModel)
    }

    override fun getForumFilter(token: String): Single<MLForumFilterResponse> {
        return dataSource.getForumFilter(token)
    }

    override fun getKnowledgeForum(
        token: String,
        sortBy: String,
        category: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLForumResponse> {
        return dataSource.getKnowledgeForum(token,sortBy,category,fromRow,limitRow)
    }

    override fun getDetailForum(
        token: String,
        discussionId: Int,
        sortBy: String,
        category: String
    ): Single<MLDetailForumResponse> {
        return dataSource.getDetailForum(token,discussionId,sortBy,category)
    }

    override fun getForumAutoCompleteSearch(keyword: String): Flowable<MLForumAutoCompleteResponse> {
        return dataSource.getForumAutoCompleteKeyword(keyword)
    }

    override fun getForumSearch(
        keyword: String,
        token: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLForumResponse> {
        return dataSource.getForumSearch(keyword,token,fromRow,limitRow)
    }

    override fun postRatingCourse(model: MLPostRatingCourseRequest): Single<MLPostRatingCourseResponse> {
        return dataSource.postRatingCourse(model)
    }

    override fun getForumIsSubscribed(
        token: String,
        discussionId: Int,
        userId: Int
    ): Single<MLForumCheckSubscribedResponse> {
        return dataSource.getForumIsSubscribed(token,discussionId,userId)
    }

    override fun postForumSubscribe(model: MLSubsUnsubsForumRequest): Single<MLSubsUnsubsForumResponse> {
        return dataSource.postForumSubscribe(model)
    }

    override fun postForumUnsubscribe(model: MLSubsUnsubsForumRequest): Single<MLSubsUnsubsForumResponse> {
        return dataSource.postForumUnsubscribe(model)
    }

    override fun getForumComments(
        discussionId: Int,
        token: String,
        sortType: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLForumListCommentsResponse> {
        return dataSource.getForumComments(discussionId,token,sortType,fromRow,limitRow)
    }

    override fun getCourseRating(courseId: Int, token: String): Single<MLGetCourseRatingResponse> {
        return dataSource.getCourseRating(courseId,token)
    }

    override fun getMyDiscussion(
        userId: Int,
        token: String,
        sortBy: String,
        category: String,
        fromRow: Int,
        limitRow: Int
    ): Flowable<MLForumResponse> {
        return dataSource.getMyDiscussion(userId,token,sortBy,category,fromRow,limitRow)
    }

    override fun postSimpleForumComment(model: MLSImpleForumCommentRequest): Single<MLSimpleForumCommentResponse> {
        return dataSource.postSimpleForumComment(model)
    }

    override fun getForumLikeStatus(
        token: String,
        discussionId: Int,
        postId: Int,
        userId: Int
    ): Single<MLForumCheckLikeResponse> {
        return dataSource.getForumLikeStatus(token, discussionId, postId, userId)
    }

    override fun getDiscussionCategories(): Single<MLDiscussionCategoriesResponse> {
        return dataSource.getDiscussionCategories()
    }

    override fun postCreateDiscussion(model: MLCreateDiscussionRequest): Single<MLCreateDiscussionResponse> {
        return dataSource.postCreateDiscussion(model)
    }

    override fun postEditDiscussion(discussionId : String,token : String, model: MLEditDiscussionRequest): Single<MLCreateDiscussionResponse> {
        return dataSource.postEditDiscussion(discussionId,token,model)
    }

    override fun deleteDiscussion(discussionId : String,token : String): Single<MLCreateDiscussionResponse> {
        return dataSource.deleteDiscussion(discussionId,token)
    }

    override fun updatePost(postId : String,token : String, model:MLEditCommentRequest): Single<MLCreateDiscussionResponse> {
        return dataSource.updatePost(postId,token, model)
    }

    override fun deletePost(postId : String,token : String): Single<MLCreateDiscussionResponse> {
        return dataSource.deletePost(postId,token)
    }

    override fun sendLikeForum(
        token: String,
        userId: Int,
        discussionId: Int,
        postId: Int,
        like: Int
    ): Single<MLSendLikeForumResponse> {
        return dataSource.sendLikeForum(token,userId,discussionId,postId, like)
    }

    override fun closeMyDiscussion(model: MLCloseDiscussionRequest): Single<MLCloseDiscussionResponse> {
        return dataSource.closeMyDiscussion(model)
    }

    override fun setEnrolCompleteModuleNS(
        courseId: Int,
        moduleId: Int,
        token: String,
        userId: Int
    ): Single<MLCourseModuleCompletionNonScormResponse> {
        return dataSource.setEnrolCompletionModuleNS(courseId,moduleId,token,userId)
    }

    override fun startCourse(
        courseId: Int,
        userEnroll: Int,
        token: String
    ): Single<MLUserEnrollResponse> {
        return dataSource.startEnroll(courseId, userEnroll,token)
    }

    override fun getNotifications(token: String, userId: Int, fromRow: Int,limitRow: Int): Flowable<MLNotificationResponse> {
        return dataSource.getNotifications(token, userId, fromRow, limitRow)
    }

    override fun markNotificationAsRead(id: Int, token: String, userId: Int): Single<MLNotificationReadResponse> {
        return dataSource.markNotificationAsRead(id, token, userId)
    }

    override fun reportForum(request: MLReportDiscussionRequest, postid: Int, discussionid: Int, token: String): Single<MLReportDiscussionResponse> {
        return dataSource.reportForum(request, postid,discussionid, token)
    }

    override fun setPoint(
        courseId: Int,
        moduleId: Int,
        token: String,
        userId: Int,
        action: String
    ): Single<MLSetPointResponse> {
        return dataSource.setPoint(courseId, moduleId, token, userId, action)
    }

    override fun registerDeviceId(request: MLRegisterDeviceIdRequest): Single<MLRegisterDeviceIdResponse> {
        return dataSource.registerDeviceId(request)
    }

    override fun getStaticSlider(): Flowable<MLStaticSliderResponse> {
        return dataSource.getStaticSlider()
    }
}