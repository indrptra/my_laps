package id.indosat.ml.common.model.response

data class MLGetCourseRatingResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultGetCourseRating?=null,
    val validtoken: Boolean
)

data class ResultGetCourseRating(
    val courseid: Int,
    val rating: Double
)