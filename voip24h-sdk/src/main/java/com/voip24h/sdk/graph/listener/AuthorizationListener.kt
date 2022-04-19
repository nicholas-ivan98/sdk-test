package com.voip24h.sdk.graph.listener

import com.voip24h.sdk.graph.model.OAuth
import java.lang.Exception

interface AuthorizationListener {
    fun success(oauth: OAuth)
    fun failed(exception: Exception)
}