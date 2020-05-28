package id.indosat.ml.common.model.response

data class MLLoginResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: ResultLogin?=null
)

data class ResultLogin(
    val address: String,
    val auth: String,
    val city: String,
    val confirmed: Boolean,
    val country: String,
    val customfields: List<CustomfieldLogin>,
    val department: String,
    val description: String,
    val descriptionformat: Int,
    val email: String,
    val firstaccess: Int,
    val firstname: String,
    val fullname: String,
    val id: Int,
    val idnumber: String,
    val institution: String,
    val lang: String,
    val lastaccess: Int,
    val lastname: String,
    val mailformat: Int,
    val phone2: String,
    val privatetoken: Any,
    val profileimageurl: String,
    val profileimageurlsmall: String,
    val suspended: Boolean,
    val theme: String,
    val timezone: String,
    val token: String,
    val username: String
)

data class CustomfieldLogin(
    val name: String,
    val shortname: String,
    val type: String,
    val value: String
)