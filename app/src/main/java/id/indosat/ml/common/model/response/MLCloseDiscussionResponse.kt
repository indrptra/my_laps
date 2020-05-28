package id.indosat.ml.common.model.response

data class MLCloseDiscussionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultCloseDiscussion?=null,
    val validtoken: Boolean
)

data class ResultCloseDiscussion(
    val id: String,
    val isclosed: Boolean,
    val name: String,
    val timemodified: String
)

data class MLReportDiscussionResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val validtoken: Boolean
)