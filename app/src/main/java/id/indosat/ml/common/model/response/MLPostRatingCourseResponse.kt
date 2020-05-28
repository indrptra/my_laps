package id.indosat.ml.common.model.response

data class MLPostRatingCourseResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultPostRating?=null,
    val validtoken: Boolean
)

data class ResultPostRating(
    val courseid: Int,
    val rating: Int,
    val userid: Int
)