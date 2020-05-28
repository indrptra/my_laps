package id.indosat.ml.common.model.response

data class MLSetPointResponse(val error: Boolean, val code: Number, val message: String, val result: SetPointResult?)

data class SetPointResult(val id: String?, val points: String?, val rule: String?)