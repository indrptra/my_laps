package id.indosat.ml.common.model.request

data class MLCreateDiscussionRequest(
    val category: String,
    val message: String,
    val subject: String,
    val token: String
)