package id.indosat.ml.common.model.request

data class MLPostCommentRequest(
    val comment: String,
    val courseid: Int,
    val token: String,
    val userid: Int
)