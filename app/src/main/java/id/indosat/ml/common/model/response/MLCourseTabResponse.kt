package id.indosat.ml.common.model.response

data class CourseResponse(
    val error: Boolean?,
    val code: Number?,
    val paging: Paging?,
    val message: String?,
    val validtoken: Boolean,
    val result: List<RowCourse>?
)

data class Paging(val page: Int?, val last_page: Int?, val limit: String?, val total_rows: Int?)