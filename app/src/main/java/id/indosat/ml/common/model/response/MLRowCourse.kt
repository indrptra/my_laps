package id.indosat.ml.common.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class RowCourse(
    val id: Int,
    val categoryid: Number?,
    val categoryname: String?,
    val idnumber: String?,
    val fullname: String?,
    val shortname: String?,
    val summary: String?,
    val imageready: Boolean?,
    val imagemimetype: String?,
    val imagefileurl: String?,
    val enrolments: Number?,
    val rating: Double,
    val startdate: String?,
    val enddate: String?,
    val progress: Double,
    val course_type: String?
): Parcelable, Serializable