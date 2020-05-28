package id.indosat.ml.common.model.response

data class MLMainMenuResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultMainMenu?=null,
    val validtoken: Boolean
)

data class ResultMainMenu(
    val numfound: Int,
    val rows: List<RowMainMenu>
)

data class RowMainMenu(
    val coursecount: Int,
    val depth: Int,
    val id: Int,
    val name: String,
    val parent: Int,
    val visible: Int,
    val iconurl:String
)