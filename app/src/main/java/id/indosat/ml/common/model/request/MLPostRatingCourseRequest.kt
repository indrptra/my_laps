package id.indosat.ml.common.model.request

data class MLPostRatingCourseRequest(
    val courseid: Int,
    val rating: Int,
    val token: String,
    val userid: Int
)