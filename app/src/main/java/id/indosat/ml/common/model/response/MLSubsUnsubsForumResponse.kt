package id.indosat.ml.common.model.response

data class MLSubsUnsubsForumResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSubsUnsubsForum?=null,
    val validtoken: Boolean
)

data class ResultSubsUnsubsForum(
    val discussionid: Int,
    val preference: String,
    val userid: Int
)