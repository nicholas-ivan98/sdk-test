package com.voip24h.sdk.graph

import android.util.Log
import com.github.kittinunf.fuel.core.Method as MethodType
import com.voip24h.sdk.graph.utils.Method
import com.voip24h.sdk.graph.auth.Authorization
import com.voip24h.sdk.graph.listener.AuthorizationListener
import com.voip24h.sdk.graph.listener.RequestListener

object GraphManager {

    fun getAccessToken(apiKey: String, apiSecret: String, listener: AuthorizationListener) {
        Authorization.sendRequest(apiKey, apiSecret, listener)
    }

    fun sendRequest(
        method: Method,
        endpoint: String,
        token: String,
        params: List<Pair<String, Any>>? = null,
        listener: RequestListener
    ) {
        try {
            Graph.sendRequest(MethodType.valueOf(method.value), endpoint, token, params, listener)
        } catch (e: Exception) {
            Log.e(this::class.java.name, e.message.toString())
        }
    }
}