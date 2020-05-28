package id.indosat.ml.common.model.response

data class MLMyCourseResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultMyCourse?=null,
    val validtoken: Boolean
)

data class ResultMyCourse(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowCourse>,
    val startindex: Int
)

