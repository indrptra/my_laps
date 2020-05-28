package id.indosat.ml.common.model

import com.chibatching.kotpref.KotprefModel

object MLPrefModel : KotprefModel() {
    var key = ""
    var isUserLoggedIn by booleanPref(false)
    var userToken by stringPref("")
    var userId by intPref(-1)
    var userEmail by stringPref("")
    var userFullName by stringPref("")
    var userAddress by stringPref("")
    var userDept by stringPref("")
    var userInstitution by stringPref("")
    var userIdEmployee by stringPref("")
    var isUserSuspended by booleanPref(false)
    var isUserConfirmed by booleanPref(false)
    var userImageSmall by stringPref("")
    var userImageLarge by stringPref("")
    var isUserAlreadyOnBoard by booleanPref(false)
    var alreadyRating by intPref(default = 0, key = key)
    fun clearAll() {
        isUserLoggedIn = false
        userToken = ""
        userId = -1
        userEmail = ""
        userFullName = ""
        userAddress = ""
        userDept = ""
        userInstitution = ""
        userIdEmployee = ""
        isUserSuspended = false
        isUserConfirmed = false
        userImageSmall = ""
        userImageLarge = ""
    }
}

