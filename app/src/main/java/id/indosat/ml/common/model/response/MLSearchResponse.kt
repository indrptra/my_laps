package id.indosat.ml.common.model.response

data class MLSearchResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultSearch?=null,
    val validtoken: Boolean
)

data class ResultSearch(
    val endindex: Int,
    val numfound: Int,
    val rows: List<RowCourse>,
    val startindex: Int
)

