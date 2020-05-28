package id.indosat.ml.common.model.response

data class MLCourseModuleCompletionNonScormResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultModuleNS?=null,
    val validtoken: Boolean
)

data class ResultModuleNS(
    val completion: Boolean,
    val completiontime: String,
    val moduleid: Int
)