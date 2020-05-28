package id.indosat.ml.common.model.response

data class MLForumCheckLikeResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForumCheckLike?=null,
    val validtoken: Boolean
)

data class ResultForumCheckLike(
    val discussionid: Int,
    val postid: Int,
    val userid: Int
)