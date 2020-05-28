package id.indosat.ml.base

import androidx.lifecycle.ViewModel


abstract class BaseViewModel:ViewModel(){

    @PublishedApi
    internal var classTag = ""

    init {
        classTag = this.javaClass.simpleName
    }
}