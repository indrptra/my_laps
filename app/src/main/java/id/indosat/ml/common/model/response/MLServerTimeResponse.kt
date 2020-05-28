package id.indosat.ml.common.model.response


data class MLServerTimeResponse(
    val error: Boolean,
    val code: Int,
    val message: String,
    val result: ResultServerTime
)

data class ResultServerTime(
    val serverTime: String,
    val serverTimestamp: Int
)