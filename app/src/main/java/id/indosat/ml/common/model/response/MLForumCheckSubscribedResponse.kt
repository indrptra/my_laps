package id.indosat.ml.common.model.response

data class MLForumCheckSubscribedResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForumCheckSubs?=null,
    val validtoken: Boolean
)

data class ResultForumCheckSubs(
    val discussionid: Int,
    val preference: String,
    val userid: Int
)