package id.indosat.ml.common.model.response

import android.os.Parcelable
import androidx.annotation.Nullable
import kotlinx.android.parcel.Parcelize

data class MLCourseSubCategoriesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSubCategories?=null,
    val validtoken: Boolean
)

data class ResultSubCategories(
    val numfound: Int,
    val rows: List<RowSubCategory>?=null
)

@Parcelize
data class RowSubCategory(
    val coursecount: Int,
    val depth: Int,
    val id: Int,
    val name: String,
    val parent: Int,
    val visible: Int,
    @get:Nullable
    val subcats: List<RowSubCategory>?=null
): Parcelable