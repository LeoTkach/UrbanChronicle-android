package com.leotkach.urbanchronicle.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionStore(context: Context) {
    private val prefs = context.getSharedPreferences("urban_session", Context.MODE_PRIVATE)
    private val _user = MutableStateFlow(read())
    val user: StateFlow<SessionUser?> = _user.asStateFlow()

    fun current(): SessionUser? = _user.value

    fun signIn(user: SessionUser) {
        prefs.edit()
            .putLong(KEY_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .apply()
        _user.value = user
    }

    fun signOut() {
        prefs.edit().clear().apply()
        _user.value = null
    }

    private fun read(): SessionUser? {
        val id = prefs.getLong(KEY_ID, -1L)
        if (id < 0) return null
        val name = prefs.getString(KEY_NAME, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        return SessionUser(id, name, email)
    }

    companion object {
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_EMAIL = "email"
    }
}
