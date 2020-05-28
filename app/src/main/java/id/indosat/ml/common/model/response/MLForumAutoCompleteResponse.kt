package id.indosat.ml.common.model.response

data class MLForumAutoCompleteResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForumAutoComplete?=null,
    val validtoken: Boolean
)

data class ResultForumAutoComplete(
    val keywords: List<String>,
    val numfound: Int
)