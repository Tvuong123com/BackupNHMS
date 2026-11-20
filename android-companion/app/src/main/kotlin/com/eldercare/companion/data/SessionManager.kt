package com.eldercare.companion.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "nhms_care_companion_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_CNA_ID = "cna_id"
        private const val KEY_CNA_NAME = "cna_name"
        private const val KEY_CNA_ROLE = "cna_role"
        private const val KEY_CNA_EMAIL = "cna_email"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_CNA_PHONE = "cna_phone"
        private const val KEY_CNA_NOTES = "cna_notes"
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_APP_LANGUAGE = "app_language"

        const val DEFAULT_BASE_URL = "http://eldercare-nhms.duckdns.org/api/v1/"
    }

    var authToken: String?
        get() = prefs.getString(KEY_AUTH_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_AUTH_TOKEN, value).apply()

    var sessionId: String?
        get() = prefs.getString(KEY_SESSION_ID, null)
        set(value) = prefs.edit().putString(KEY_SESSION_ID, value).apply()

    var cnaId: Long
        get() = prefs.getLong(KEY_CNA_ID, -1L)
        set(value) = prefs.edit().putLong(KEY_CNA_ID, value).apply()

    var cnaName: String?
        get() = prefs.getString(KEY_CNA_NAME, null)
        set(value) = prefs.edit().putString(KEY_CNA_NAME, value).apply()

    var cnaRole: String?
        get() = prefs.getString(KEY_CNA_ROLE, null)
        set(value) = prefs.edit().putString(KEY_CNA_ROLE, value).apply()

    var cnaEmail: String?
        get() = prefs.getString(KEY_CNA_EMAIL, null)
        set(value) = prefs.edit().putString(KEY_CNA_EMAIL, value).apply()

    var userPassword: String?
        get() = prefs.getString(KEY_USER_PASSWORD, null)
        set(value) = prefs.edit().putString(KEY_USER_PASSWORD, value).apply()

    var cnaPhone: String
        get() = prefs.getString(KEY_CNA_PHONE, "0912 345 678") ?: "0912 345 678"
        set(value) = prefs.edit().putString(KEY_CNA_PHONE, value).apply()

    var cnaNotes: String
        get() = prefs.getString(KEY_CNA_NOTES, "Phụ trách chăm sóc phân khu Lão khoa Tầng 1 (P101 - P104)") ?: ""
        set(value) = prefs.edit().putString(KEY_CNA_NOTES, value).apply()

    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) {
            val formatted = if (value.endsWith("/")) value else "$value/"
            prefs.edit().putString(KEY_BASE_URL, formatted).apply()
        }

    var appLanguage: String
        get() = prefs.getString(KEY_APP_LANGUAGE, "vi") ?: "vi"
        set(value) = prefs.edit().putString(KEY_APP_LANGUAGE, value).apply()

    val isLoggedIn: Boolean
        get() = !authToken.isNullOrBlank()

    fun saveLogin(token: String, sessionId: String, role: String, name: String) {
        prefs.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .putString(KEY_SESSION_ID, sessionId)
            .putString(KEY_CNA_ROLE, role)
            .putString(KEY_CNA_NAME, name)
            .apply()
    }

    fun saveCurrentUser(id: Long, email: String, fullName: String, role: String) {
        prefs.edit()
            .putLong(KEY_CNA_ID, id)
            .putString(KEY_CNA_EMAIL, email)
            .putString(KEY_CNA_NAME, fullName)
            .putString(KEY_CNA_ROLE, role)
            .apply()
    }

    fun clearSession() {
        val currentBaseUrl = baseUrl
        prefs.edit().clear().apply()
        // Preserve user configured base URL across logouts
        baseUrl = currentBaseUrl
    }
}
