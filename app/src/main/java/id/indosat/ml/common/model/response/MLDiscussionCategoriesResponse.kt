package id.indosat.ml.common.model.response

data class MLDiscussionCategoriesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: List<String>?=null,
    val validtoken: Boolean
)