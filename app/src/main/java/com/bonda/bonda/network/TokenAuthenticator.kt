package com.bonda.bonda.network

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.bonda.bonda.network.model.auth.ReissueRequest
import com.bonda.bonda.model.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * 만료된 accessToken을 재발급합니다
 */
class TokenAuthenticator(
    private val context: Context,
    private val accessTokenProvider: AccessTokenProvider
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) {
            return null
        }

        return synchronized(this) {
            val currentToken = accessTokenProvider.getAccessToken()
            val oldToken = response.request.header("Authorization")?.substringAfter("Bearer ")

            if (currentToken != null && oldToken != currentToken) {
                return@synchronized response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            val refreshToken = prefs.getString(PREF_KEY_REFRESH_TOKEN, null)
                ?: return@synchronized null

            val newTokens = runBlocking {
                try {
                    val res = ApiClient.authService
                        .reissueAccessToken(ReissueRequest(refreshToken))
                        .unwrapOrThrow()
                    res.accessToken
                } catch (e: Exception) {
                    Log.e(TAG, "TokenAuthentication.kt::authenticate", e)
                    null
                }
            }

            newTokens?.let {
                accessTokenProvider.setAccessToken(it)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${it}")
                    .build()
            }
        }
    }

}