package id.indosat.ml.common.model.response

data class MLDetailForumResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultDFR,
    val validtoken: Boolean
)

data class ResultDFR(
    val next: Int,
    val previous: Int,
    val rows: RowsDFR
)

data class RowsDFR(
    val attachment: Boolean,
    val attachments: List<AttachmentDFR>,
    val category: String,
    val created: String,
    val createdby: String,
    val creatorprofilepicture: String,
    val discussionid: Int,
    val id: Int,
    val islocked: Boolean,
    val lastmodified: String,
    val lastmodifiedby: String,
    val message: String,
    val name: String,
    val numlikes: Int,
    val numreplies: Int,
    val numsubscriber: Int
)

data class AttachmentDFR(
    val filename: String,
    val filepath: String,
    val filesize: Int,
    val fileurl: String,
    val isexternalfile: Boolean,
    val mimetype: String,
    val timemodified: Int
)