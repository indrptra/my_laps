package id.indosat.ml.common.model.response

data class MLMyPointsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultMyPoint?=null,
    val validtoken: Boolean
)

data class ResultMyPoint(
    val points: Int,
    val userid: Int
)