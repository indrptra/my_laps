package id.indosat.ml.common.model.response

data class MLGetCommentsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultComment?=null,
    val validtoken: Boolean
)

data class ResultComment(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowComments>,
    val startindex: String
)

data class RowComments(
    val avatar: String,
    val content: String,
    val subject: String,
    val message: String,
    val delete: Boolean,
    val fullname: String,
    val id: Int,
    val postdate: String,
    val userid: Int
)