package id.indosat.ml.common.model.response

import com.google.gson.annotations.SerializedName

data class MLUserEnrollResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: MLUserEnrollResult
)

data class MLUserEnrollResult(
    val status: Int,
    @SerializedName("enrollId")
    val enrollId: Int,
    @SerializedName("userid")
    val userId: Int
)