package id.indosat.ml.common.model.response

data class MLRegisterDeviceIdResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultRegisterDeviceId?=null,
    val validtoken: Boolean
)

data class ResultRegisterDeviceId(
    val deviceId: String,
    val userid: Int
)