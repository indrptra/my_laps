package id.indosat.ml.common.model.response

import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MLHomeSliderResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultHomeSlider?=null,
    val validtoken: Boolean
):Parcelable


@Parcelize
data class ResultHomeSlider(
    val numfound: Int,
    val rows: List<RowHomeSlider>
):Parcelable

@Parcelize
data class RowHomeSlider(
    val sliderid:Int,
    val type:String,
    val id: Int,
    val url: String
):Parcelable