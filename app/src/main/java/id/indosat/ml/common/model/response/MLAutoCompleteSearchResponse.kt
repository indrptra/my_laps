package id.indosat.ml.common.model.response

data class MLAutoCompleteSearchResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultAutoCompleteSearch?=null,
    val validtoken: Boolean
)

data class ResultAutoCompleteSearch(
    val keywords: List<String>,
    val numfound: Int
)