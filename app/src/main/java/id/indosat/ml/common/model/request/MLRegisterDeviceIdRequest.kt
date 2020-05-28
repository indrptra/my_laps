package id.indosat.ml.common.model.request

data class MLRegisterDeviceIdRequest (
    val token: String,
    val deviceid: String
)