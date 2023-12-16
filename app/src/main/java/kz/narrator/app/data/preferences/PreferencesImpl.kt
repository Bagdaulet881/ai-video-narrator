package kz.narrator.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kz.narrator.app.data.models.user.User

class PreferencesImpl(
    private val context: Context
) : Preferences {

    companion object {
        private const val UNIQUE_ID = "unique_id"
        private const val LANGUAGE = "language"
        private const val APP_TOKEN = "app_token"
        private const val AUTH_TOKEN = "auth_token"
        private const val CURRENT_USER = "current_user"
        private const val MAIL = "email"
        private const val LAT = "lat"
        private const val LNG = "lng"
        private const val LOCATION_STATE = "location_state"
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var shPr: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    var gson = Gson()

    init {
        shPr = context.getSharedPreferences("PreferencesImpl", Context.MODE_PRIVATE)
        editor = shPr.edit()
    }

    /**
     * DEVICE UNIQUE ID
     */
    override fun setUniqueID(uniqueID: String?) {
        editor.putString(UNIQUE_ID, uniqueID)
        editor.commit()
    }

    override fun getUniqueID(): String? = shPr.getString(UNIQUE_ID, null)

    /**
     * LANGUAGE
     */
    override fun setLanguage(language: String) {
        editor.putString(LANGUAGE, language)
        editor.commit()
    }

    override fun getLanguage(): String? {
        return shPr.getString(LANGUAGE, null)
    }

    /**
     *
     */
    override fun setAppToken(appToken: String) {
        editor.putString(APP_TOKEN, appToken)
        editor.commit()
    }

    override fun getAppToken(): String? {
        return shPr.getString(AUTH_TOKEN, null)
    }

    override fun removeAppToken() {
        editor
            .remove(APP_TOKEN)
            .commit()
    }
    /**
     *
     */
    override fun setCurrentUser(user: User) {
        val jsonString = gson.toJson(user)
        editor.putString(CURRENT_USER, jsonString)
        editor.commit()
    }

    override fun getCurrentUser(): User? {
        return gson.fromJson(shPr.getString(CURRENT_USER, null), User::class.java)
    }

    override fun setAuthToken(token: String) {
        editor.putString(AUTH_TOKEN, token)
        editor.commit()
    }

    override fun getAuthToken(): String? {
        return shPr.getString(AUTH_TOKEN, null)
    }

    override fun removeAuthToken() {
        mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        editor.remove(CURRENT_USER).commit()
        editor
            .remove(AUTH_TOKEN)
            .commit()
    }

    override fun setEmail(email: String) {
        editor.putString(MAIL, email)
        editor.commit()
    }

    override fun getEmail(): String? {
        return shPr.getString(MAIL, null)
    }

    override fun deleteEmail() {
        editor
            .remove(MAIL)
            .commit()
    }

    override fun setLocationData(lat: Double, lng: Double) {
        editor.putString(LAT, lat.toString())
        editor.putString(LNG, lng.toString())
        editor.putBoolean(LOCATION_STATE, true)
        editor.commit()
    }

    override fun getLocationData(): ArrayList<Double> {
        var res = ArrayList<Double>()
        res.add(shPr.getString(LAT, "0.0")!!.toDouble())
        res.add(shPr.getString(LNG, "0.0")!!.toDouble())
        return res
    }

    override fun removeLocationData() {
        editor.remove(LAT)
        editor.remove(LNG)
        editor.remove(LOCATION_STATE)
        editor.commit()
    }

    override fun getLocationDataState(): Boolean {
        return shPr.getBoolean(LOCATION_STATE, false)
    }

}