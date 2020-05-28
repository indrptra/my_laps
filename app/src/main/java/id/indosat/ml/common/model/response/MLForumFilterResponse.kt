package id.indosat.ml.common.model.response

data class MLForumFilterResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultForumFilter?=null,
    val validtoken: Boolean
)

data class ResultForumFilter(
    val categories: List<CategoryFF>,
    val sortby: List<SortbyFF>
)

data class SortbyFF(
    val label: String,
    val value: String
)

data class CategoryFF(
    val label: String,
    val value: String
)