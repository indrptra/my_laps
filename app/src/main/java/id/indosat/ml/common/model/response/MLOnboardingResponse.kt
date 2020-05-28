package id.indosat.ml.common.model.response

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MLOnboardingResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultOnBoarding?=null,
    val validtoken: Boolean
):Parcelable

@Parcelize
data class ResultOnBoarding(
    val numfound: Int,
    val rows: List<RowOnBoarding>
):Parcelable


@Parcelize
data class RowOnBoarding(
    val filetype: String,
    val id: Int,
    val type: String,
    val url: String
):Parcelable