package id.indosat.ml.common.ml_enum

enum class MLESortCourseType(val type:String){
    POPULAR("popular"),
    LATEST("latest"),
    TOPRATED("toprated")
}

enum class MLESliderType(val type:String){
    COURSE("course"),
    DISCUSSION("discussion"),
    CATEGORY_COURSE("category")
}