package kz.narrator.app.data.preferences

import kz.narrator.app.data.models.user.User

interface Preferences {

    /**
     *
     */
    fun setUniqueID(uniqueID: String?)

    fun getUniqueID(): String?

    fun setLanguage(language: String)

    fun getLanguage(): String?

    /**
     *
     */
    fun setAppToken(appToken: String)

    fun getAppToken(): String?

    fun removeAppToken()
    /**
     *
     */
    fun setCurrentUser(user: User)

    fun getCurrentUser(): User?

    fun setAuthToken(token: String)

    fun getAuthToken(): String?

    fun removeAuthToken()

    /**
     *
     */

    fun setEmail(email: String)

    fun getEmail(): String?

    fun deleteEmail()

    /**
     *
     */

    fun setLocationData(lat: Double, lng: Double)

    fun getLocationData(): ArrayList<Double>

    fun getLocationDataState(): Boolean

    fun removeLocationData()
}