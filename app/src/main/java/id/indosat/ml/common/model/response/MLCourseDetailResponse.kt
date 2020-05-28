package id.indosat.ml.common.model.response

/*data class MLCourseDetailResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultCourseDetail?=null,
    val validtoken: Boolean
)

data class ResultCourseDetail(
    val numfound: Int,
    val rows: RowsCourseDetail?=null
)

data class RowsCourseDetail(
    val categoryid: Int,
    val categoryname: String,
    val enddate: String,
    val enrolments: Int,
    val fullname: String,
    val id: Int,
    val idnumber: String,
    val imagefileurl: String,
    val imagemimetype: String,
    val imageready: Boolean,
    val rating: Double,
    val shortname: String,
    val startdate: String,
    val summary: String
)*/

data class MLCourseDetailResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultCourseDetail?=null,
    val validtoken: Boolean
)

data class ResultCourseDetail(
    val numfound: Int,
    val rows: RowsCourseDetail
)

data class RowsCourseDetail(
    val categoryid: Int,
    val categoryname: String,
    val enddate: String,
    val enrolments: Int,
    val fullname: String,
    val id: Int,
    val idnumber: String,
    val imagefileurl: String,
    val imagemimetype: String,
    val imageready: Boolean,
    val isScorm: Boolean,
    val modules: List<ModuleCourseDetail>,
    var rating: Double,
    val shortname: String,
    val startdate: String,
    val summary: String
)

data class ModuleCourseDetail(
    val content: ContentModuleCourseDetail?=null,
    val id: Int,
    val name: String,
    val thumbnail: String,
    val type: String,
    val usercompletion: UsercompletionCourseDetail?=null
)

data class UsercompletionCourseDetail(
    val completionstate: Boolean,
    val viewed: Boolean
)

data class ContentModuleCourseDetail(
    val filename: String,
    val filetype: String,
    val fileurl: String
)