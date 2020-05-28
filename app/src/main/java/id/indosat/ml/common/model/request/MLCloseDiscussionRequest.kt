package id.indosat.ml.common.model.request

data class MLCloseDiscussionRequest(
    val discussionid: Int,
    val token: String,
    val userid: Int
)

data class MLReportDiscussionRequest(
    val category: String,
    val reason: String
)

