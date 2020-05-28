package id.indosat.ml.common.model.response

data class MLScormUrlReqResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ScormUrlResult?=null,
    val validtoken: Boolean
)

data class ScormUrlResult(
    val url: String
)