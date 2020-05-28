package id.indosat.ml.common.ml_enum

enum class MLEMyCourse(val type:String){
    RUNNING("running"),
    COMPLETED("completed"),
    FUTURE("future"),
    EMPTY("empty")
}

enum class MLECourseDetailModuleType(val type:String){
    SCORM("scorm"),
    URL("url"),
    RESOURCE("resource"),
    VIDEO("video"),
    PDF("pdf"),
    CERTIFICATE("certificate"),
    QUIZ("quiz")
}