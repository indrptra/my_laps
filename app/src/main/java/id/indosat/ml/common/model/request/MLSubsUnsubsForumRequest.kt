package id.indosat.ml.common.model.request

data class MLSubsUnsubsForumRequest(
    val discussionid: Int,
    val token: String,
    val userid: Int
)