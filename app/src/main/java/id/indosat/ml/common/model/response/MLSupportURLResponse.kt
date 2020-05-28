package id.indosat.ml.common.model.response

data class MLSupportURLResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSupportURL?=null,
    val validtoken: Boolean
)

data class ResultSupportURL(
    val url: String
)