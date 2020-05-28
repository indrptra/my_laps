package id.indosat.ml.common.model.request

data class MLEditDiscussionRequest(
    val category: String,
    val message: String,
    val title: String,
    val token: String,
    val user_id: String
)