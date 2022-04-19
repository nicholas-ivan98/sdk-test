package com.voip24h.sdk.graph.auth

import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.voip24h.sdk.graph.listener.AuthorizationListener
import com.voip24h.sdk.graph.model.OAuth
import org.json.JSONObject

internal object Authorization {

    private const val URL_AUTH = "http://auth2.voip24h.vn/api/token"

    fun sendRequest(apiKey: String, apiSecret: String, listener: AuthorizationListener) {
        URL_AUTH.httpPost().jsonBody(
            JSONObject(
                mapOf(
                    "api_key" to apiKey,
                    "api_secert" to apiSecret
                )
            ).toString()
        ).responseObject(OAuth.Deserializer()) { result ->
            when (result) {
                is Result.Success -> listener.success(result.value)
                is Result.Failure -> listener.failed(result.error)
            }
        }
    }
}