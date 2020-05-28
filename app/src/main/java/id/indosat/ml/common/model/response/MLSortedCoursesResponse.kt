package id.indosat.ml.common.model.response


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class MLSortedCoursesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSortedCourse?=null,
    val validtoken: Boolean
)

@Parcelize
data class ResultSortedCourse(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowCourse>,
    val startindex: Int
):Parcelable,Serializable

