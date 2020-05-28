package id.indosat.ml.core.network

import id.indosat.ml.common.model.request.*
import id.indosat.ml.common.model.response.*
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.*

interface IMLNetworkService{
    @GET("v1/utilities/servertime")
    fun getServerTime():Single<MLServerTimeResponse>

    @GET("v1/utilities/onboarding")
    fun getOnBoarding():Single<MLOnboardingResponse>

    @GET("v1/login")
    fun login(@Query("username") username:String,@Query("password") password:String):Single<MLLoginResponse>



    @Headers("Content-Type: application/json")
    @POST("v1/login/post")
    fun postLogin(@Body model:MLLoginRequest):Single<MLLoginResponse>

    @GET("v1/course/maincategories")
    fun getMainMenu(@Query("token") token:String,@Query("icon") icon:String="1"):Single<MLMainMenuResponse>

    @GET("v1/course/sorted")
    fun getSortedCourses(@Query("token")token: String,
                         @Query("sortby") sortBy:String) :Single<MLSortedCoursesResponse>

    @GET("v1/course/sorted")
    fun getSortedCoursesLimit(@Query("token")token: String,
                            @Query("sortby") sortBy:String,
                              @Query("fromrow") fromRow:Int,
                              @Query("limitrow") limitRow:Int) :Flowable<MLSortedCoursesResponse>

    @GET("v1/utilities/slidersapps")
    fun getHomeSlider():Single<MLHomeSliderResponse>

    @GET("v1/course/autocomplete")
    fun getAutoCompleteSearch(@Query("token") token: String,
                              @Query("keyword") keyword:String):Single<MLAutoCompleteSearchResponse>

    @GET("v1/course/find")
    fun getSearchResult(@Query("token") token: String,
                        @Query("keyword") keyword: String,
                        @Query("fromrow") fromRow: Int,
                        @Query("limitrow") limitRow: Int):Flowable<MLSearchResponse>

    @GET("v1/course/categorized")
    fun getCoursePerCat(@Query("token") token: String,
                        @Query("category") category:Int,
                        @Query("fromrow") fromRow: Int,
                        @Query("limitrow") limitRow: Int):Flowable<MLCoursePerCatResponse>

    @GET("v1/course/subcategories")
    fun getCourseSubCategories(@Query("token") token: String,
                               @Query("category") category: Int):Single<MLCourseSubCategoriesResponse>

    @GET("v1/member/mypoints")
    fun getMyPoints(@Query("username") username: String):Single<MLMyPointsResponse>

    @GET("v1/member/dashboard")
    fun getUserDashboard(@Query("token") token: String,
                         @Query("username") username: String):Single<MLDashboardResponse>

    @GET("v1/member/leaderboard")
    fun getLeaderBoard(@Query("token") token: String,
                       @Query("username")username: String):Single<MLLeaderBoardResponse>

    @GET("v1/course/detail")
    fun getCourseDetail(@Query("token") token: String,
                        @Query("id") id:Int,
                        @Query("showmodules") showModules:Int,
                        @Query("user") user:Int):Single<MLCourseDetailResponse>

    @GET("v1/webview/urlscorm")
    fun getScormURLReq(@Query("token") token: String,
                       @Query("id") courseId:Int,
                       @Query("user") userId:Int):Single<MLScormUrlReqResponse>

    @GET("v1/webview/urlsupport")
    fun getURLSupport():Single<MLSupportURLResponse>


    @GET("v1/member/mycourses")
    fun getMyCourses(@Query("token") token: String,
                     @Query("userid") userid:String,
                     @Query("type") type:String,
                     @Query("fromrow") fromRow: Int,
                     @Query("limitrow") limitRow: Int):Flowable<MLMyCourseResponse>

    @GET("v1/course/courseoverview")
    fun getCoursesByType(@Query("token") token: String,
                         @Query("user") userid: String,
                         @Query("type") type: String,
                         @Query("page") page: Int,
                         @Query("limit") limit: Int = 10):Flowable<CourseResponse>

    @GET("v1/course/courseoverview")
    fun getMyCoursesCompleted(@Query("token") token: String,
                     @Query("user") userid:String):Flowable<MLMyCourseCompletedResponse>

    @GET("v1/course/comments")
    fun getCourseComments(@Query("token") token: String,
                          @Query("course")course:Int,
                          @Query("fromrow") fromRow: Int,
                          @Query("limitrow") limitRow: Int):Flowable<MLGetCommentsResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/course/postcomment")
    fun postCourseComment(@Body postModel:MLPostCommentRequest):Single<MLPostCommentResponse>

    @GET("v1/forum/filter")
    fun getForumFilter(@Query("token") token: String):Single<MLForumFilterResponse>

    @GET("v1/forum")
    fun getKnowledgeForum(@Query("token") token:String,
                          @Query("sortby") sortBy: String,
                          @Query("category") category: String,
                          @Query("fromrow") fromRow: Int,
                          @Query("limitrow") limitRow: Int):Flowable<MLForumResponse>

    @GET("v1/forum/discussiondetail")
    fun getDetailForum(@Query("token") token:String,
                       @Query("discussionid") discussionId:Int,
                       @Query("sortby") sortBy: String,
                       @Query("category") category: String):Single<MLDetailForumResponse>

    @GET("v1/forum/autocompletes")
    fun getForumSearchAutoComplete(@Query("keyword") keyword: String):Flowable<MLForumAutoCompleteResponse>


    @GET("v1/forum/find")
    fun getForumSearch(@Query("keyword") keyword: String,
                       @Query("token") token: String,
                       @Query("fromrow") fromRow: Int,
                       @Query("limitrow") limitRow: Int):Flowable<MLForumResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/course/ratecourse")
    fun postRatingCourse(@Body postRatingModel:MLPostRatingCourseRequest):Single<MLPostRatingCourseResponse>

    @GET("v1/forum/subscribed")
    fun getForumSubscribedStatus(@Query("token") token: String,
                                 @Query("discussionid") discussionId: Int,
                                 @Query("userid") userId: Int):Single<MLForumCheckSubscribedResponse>


    @Headers("Content-Type: application/json")
    @POST("v1/forum/subscribe")
    fun postSubsForum(@Body postRatingModel:MLSubsUnsubsForumRequest):Single<MLSubsUnsubsForumResponse>


    @Headers("Content-Type: application/json")
    @POST("v1/forum/unsubscribe")
    fun postUnSubsForum(@Body postRatingModel:MLSubsUnsubsForumRequest):Single<MLSubsUnsubsForumResponse>


    @GET("v1/forum/posts")
    fun getForumComments(@Query("discussionid") discussionId: Int,
                         @Query("token") token: String,
                         @Query("sort") sort:String,
                         @Query("fromrow") fromRow: Int,
                         @Query("limitrow") limitRow: Int):Flowable<MLForumListCommentsResponse>


    @GET("v1/course/rate")
    fun getCourseRating(@Query("courseid") courseId: Int,
                        @Query("token") token: String):Single<MLGetCourseRatingResponse>


    @GET("v1/member/mydiscussion")
    fun getMyDiscussion(@Query("userid") userId: Int,
                        @Query("token") token: String,
                        @Query("sortby") sortBy: String,
                        @Query("category") category: String,
                        @Query("fromrow") fromRow: Int,
                        @Query("limitrow") limitRow: Int):Flowable<MLForumResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/forum/createpost")
    fun postSimpleForumComment(@Body postComment:MLSImpleForumCommentRequest):Single<MLSimpleForumCommentResponse>

    @GET("v1/forum/isliked")
    fun getForumLikeStatus(@Query("token") token: String,
                           @Query("discussionid") discussionId: Int,
                           @Query("postid") postId:Int,
                           @Query("userid") userId: Int):Single<MLForumCheckLikeResponse>



    @GET("v1/forum/like")
    fun sendLikedForum(@Query("token") token: String,
                       @Query("userid") userId:Int,
                       @Query("discussionid") discussionId: Int,
                       @Query("postid") postId:Int,
                       @Query("like") like:Int):Single<MLSendLikeForumResponse>

    @GET("v1/forum/discussioncategories")
    fun getDiscussionCategories():Single<MLDiscussionCategoriesResponse>


    @Headers("Content-Type: application/json")
    @POST("v1/forum/creatediscussion")
    fun postCreateDiscussion(@Body postDisc:MLCreateDiscussionRequest):Single<MLCreateDiscussionResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/forum/updatediscussion")
    fun postUpdateDiscussion(@Query("discussionid") disscusionId: String,
                             @Query("token") token: String,
                             @Body postDisc:MLEditDiscussionRequest):Single<MLCreateDiscussionResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/forum/deletediscussion")
    fun deleteDiscussion(@Query("discussionid") disscusionId: String,
                             @Query("token") token: String
                             ):Single<MLCreateDiscussionResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/forum/updatepost")
    fun updatePost(@Query("postid") postId: String,
                   @Query("token") token: String,
                   @Body postDisc:MLEditCommentRequest
                             ):Single<MLCreateDiscussionResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/forum/deletepost")
    fun deletePost(@Query("postid") postId: String,
                             @Query("token") token: String
                             ):Single<MLCreateDiscussionResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/member/closemydiscussion")
    fun postCloseDiscussion(@Body postDisc:MLCloseDiscussionRequest):Single<MLCloseDiscussionResponse>

    @GET("v1/course/completion")
    fun setEnrollCompleteModuleNS(@Query("courseid") courseId: Int,
                                 @Query("moduleid") moduleId:Int,
                                 @Query("token") token: String,
                                 @Query("userid") userId: Int):Single<MLCourseModuleCompletionNonScormResponse>

    @GET("v1/member/myinbox")
    fun getNotifications(@Query("token") token: String,
                         @Query("userid") userId: Int,
                         @Query("fromrow") fromRow: Int,
                         @Query("limitrow") limitRow: Int):Flowable<MLNotificationResponse>

    @GET("v1/member/readmessage")
    fun markNotificationAsRead(   @Query("id") id: Int,
                                  @Query("token") token: String,
                                  @Query("userid") userId: Int):Single<MLNotificationReadResponse>

    @GET("v1/course/setcompletition")
    fun setEnrollCompleteModuleNSNew(@Query("courseid") courseId: Int,
                                 @Query("module") moduleId:Int,
                                 @Query("token") token: String,
                                 @Query("userid") userId: Int):Single<MLCourseModuleCompletionNonScormResponse>

    @GET("v1/course/enrol/{course_id}")
    fun startEnrollCourse(@Path("course_id") courseId: Int,
                          @Query("enrol_user") enrollUser: Int,
                          @Query("token") token: String):Single<MLUserEnrollResponse>

    @POST("v1/forum/report")
    fun reportForum(
                    @Body postDisc:MLReportDiscussionRequest,
                    @Query("postid") postid: Int,
                    @Query("discussionid") discussionid: Int,
                    @Query("token") token: String):Single<MLReportDiscussionResponse>

    @GET("v1/course/setpoint/{course_id}")
    fun setPoint(@Path("course_id") courseId: Int,
                 @Query("module_id") moduleId:Int,
                 @Query("token") token: String,
                 @Query("user") userId: Int,
                 @Query("action") action: String):Single<MLSetPointResponse>

    @Headers("Content-Type: application/json")
    @POST("v1/member/registerdevice")
    fun registerDeviceId(@Body postRequest: MLRegisterDeviceIdRequest
    ):Single<MLRegisterDeviceIdResponse>


    @GET("v1/utilities/staticslider")
    fun getStaticSlider():Flowable<MLStaticSliderResponse>
}