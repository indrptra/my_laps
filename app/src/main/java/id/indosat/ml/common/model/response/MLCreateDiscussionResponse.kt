package id.indosat.ml.common.model.response

data class MLCreateDiscussionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultCreateDiscussion?=null,
    val validtoken: Boolean
)

data class ResultCreateDiscussion(
    val discussionid: Int,
    val subject: String
)