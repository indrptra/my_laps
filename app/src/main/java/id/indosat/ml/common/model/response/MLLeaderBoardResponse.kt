package id.indosat.ml.common.model.response

data class MLLeaderBoardResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultLeaderBoard?=null,
    val validtoken: Boolean
)

data class ResultLeaderBoard(
    val numfound: Int,
    val rows: List<RowLeaderBoard>
)

data class RowLeaderBoard(
    val fullname: String,
    val level: Int,
    val number: Int,
    val points: Int,
    val userid: Int,
    val username: String
)