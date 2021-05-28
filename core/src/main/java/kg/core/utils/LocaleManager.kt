package kg.core.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

object LocaleManager {

    val LANGUAGE_KEY_RUSSIAN = "ru"
    val LANGUAGE_KEY_KYRGYZ = "ky"
    val LANGUAGE_KEy_ENGLISH = "en"

    private val LANGUAGE_KEY = "language_key"

    /**
     * set current pref locale
     * @param mContext
     * @return
     */
    fun setLocale(mContext: Context): Context {
        return updateResources(
            mContext,
            getLanguagePref(mContext)
        )
    }

    /**
     * Set new Locale with context
     * @param mContext
     * @param mLocaleKey
     * @return
     */
    fun setNewLocale(mContext: Activity, mLocaleKey: String): Context {
        setLanguagePref(mContext, mLocaleKey)
        return updateResources(
            mContext,
            mLocaleKey
        )
    }

    /**
     * Get saved Locale from SharedPreferences
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    fun getLanguagePref(mContext: Context): String? {
        val mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        return mPreferences.getString(
            LANGUAGE_KEY,
            LANGUAGE_KEY_RUSSIAN
        )
    }

    /**
     * set pref key
     * @param mContext
     * @param localeKey
     */
    private fun setLanguagePref(mContext: Context, localeKey: String) {
        val mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)
        mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply()
    }

    /**
     * update resource
     * @param context
     * @param language
     * @return
     */
    private fun updateResources(context: Context, language: String?): Context {
        var context = context
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = Configuration(res.configuration)
        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale)
            context = context.createConfigurationContext(config)
        } else {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
        return context
    }

    /**
     * get current locale
     * @param res
     * @return
     */
    fun getLocale(res: Resources): Locale {
        val config = res.configuration
        return if (Build.VERSION.SDK_INT >= 24) config.locales.get(0) else config.locale
    }
}
