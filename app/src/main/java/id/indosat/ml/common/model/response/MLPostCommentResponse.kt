package id.indosat.ml.common.model.response

data class MLPostCommentResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultPostComment?=null,
    val validtoken: Boolean
)

data class ResultPostComment(
    val comment: String,
    val courseid: Int,
    val userid: Int
)