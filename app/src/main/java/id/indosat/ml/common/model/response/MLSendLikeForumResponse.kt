package id.indosat.ml.common.model.response

data class MLSendLikeForumResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSendLikeForum?=null,
    val validtoken: Boolean
)

data class ResultSendLikeForum(
    val discussionid: Int,
    val forumid: Int,
    val postid: Int,
    val userid: Int
)