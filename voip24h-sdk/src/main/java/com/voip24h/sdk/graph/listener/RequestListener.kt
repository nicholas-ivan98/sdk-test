package com.voip24h.sdk.graph.listener

import org.json.JSONObject

interface RequestListener {
    fun success(jsonObject: JSONObject)
    fun failed(exception: Exception)
}