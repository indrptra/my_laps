package id.indosat.ml.common.model.response

data class MLCoursePerCatResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultCoursePerCat?=null,
    val validtoken: Boolean
)

data class ResultCoursePerCat(
    val endindex: Int,
    val headerimage: String,
    val numfound: Int,
    val rows: List<RowCourse>,
    val startindex: Int
)