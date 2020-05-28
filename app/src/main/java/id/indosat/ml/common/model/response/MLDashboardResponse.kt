package id.indosat.ml.common.model.response

data class MLDashboardResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultDashboard?=null,
    val validtoken: Boolean
)

data class ResultDashboard(
    val fullname: String,
    val isfinallevel: Boolean,
    val level: Int,
    val levelbadges: String,
    val levelupmessage: String,
    val nextlevel: Int,
    val nextlevelbadges: String,
    val nextlevelpoints: Int,
    val points: Int,
    val pointtogo: Int,
    val profileimageurl: String,
    val profileimageurlsmall: String,
    val userid: Int,
    val username: String
)