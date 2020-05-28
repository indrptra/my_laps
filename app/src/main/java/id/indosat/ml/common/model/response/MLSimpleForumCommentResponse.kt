package id.indosat.ml.common.model.response

data class MLSimpleForumCommentResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSimpleForumComment,
    val validtoken: Boolean
)

data class ResultSimpleForumComment(
    val postid: Int,
    val subject: String
)