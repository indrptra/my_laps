package id.indosat.ml.common.model.response

data class MLForumResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForum?=null,
    val validtoken: Boolean
)

data class ResultForum(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowForum>,
    val startindex: Int
)

data class RowForum(
    val category: String,
    val discussionid: Int,
    val id: Int,
    val islocked: Boolean,
    val can_edit: Boolean,
    val can_delete: Boolean,
    val lastmodified: String,
    val lastmodifiedby: String,
    val name: String,
    val numlikes: Int,
    val numreplies: Int,
    val numsubscriber: Int
)