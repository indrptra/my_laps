package id.indosat.ml.common.model.response


data class MLStaticSliderResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val result: List<ResultStaticSlider>,
    val validtoken: Boolean
)

data class ResultStaticSlider(
    val id: Int,
    val name: String,
    val image: String,
    val url: String
)