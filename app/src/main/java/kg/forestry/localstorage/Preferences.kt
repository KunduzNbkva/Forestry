package kg.forestry.localstorage

import android.content.Context
import android.preference.PreferenceManager

class Preferences(val context: Context) {
    val sp = PreferenceManager.getDefaultSharedPreferences(context)

    init {
        instance = this
    }

    var isFirstLoad: Boolean
        get() {
            return sp.getBoolean(IS_FIRST_LOAD, true)
        }
        set(value) {
            setSharedPrefs(IS_FIRST_LOAD, value)
        }

    var userToken: String
        get() {
            return sp.getString(USER_TOKEN, "")!!
        }
        set(value) {
            setSharedPrefs(USER_TOKEN, value)
        }
    var isAnonymous: Boolean
        get() {
            return sp.getBoolean(IS_ANONYMOUS, false)
        }
        set(value) {
            setSharedPrefs(IS_ANONYMOUS, value)
        }

    private fun setSharedPrefs(key: String, value: String?) {
        sp.edit()
            .putString(key, value)
            .apply()
    }

    private fun setSharedPrefs(key: String, value: Long) {
        sp.edit()
            .putLong(key, value)
            .commit()
    }
    private fun setSharedPrefs(key:String, value: Int) {
        sp.edit()
            .putInt(key, value)
            .commit()
    }
    private fun setSharedPrefs(key:String, value: Boolean) {
        sp.edit()
            .putBoolean(key, value)
            .commit()
    }

    fun isUserAuthorized() = this.userToken.isNotEmpty() && !this.isAnonymous


    private val USER_TOKEN = "USER_TOKEN"
    private val IS_ANONYMOUS = "IS_ANONYMOUS"

    private val IS_FIRST_LOAD = "IS_FIRST_LOAD"

    companion object {
        lateinit var instance: Preferences
    }
}