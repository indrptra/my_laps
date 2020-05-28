package id.indosat.ml.common.model.response

data class MLMyCourseCompletedResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultMyCourseCompleted?=null,
    val validtoken: Boolean
)

data class ResultMyCourseCompleted(
    val id: String,
    val fullname: String,
    val startdate: String,
    val is_assign: String,
    val assign_by: String,
    val courseid: String,
    val userid: String,
    val course_type: String,
    val progress: Int,
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowCourse>,
    val startindex: Int
)
