package com.voip24h.sdk.graph

import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.voip24h.sdk.graph.listener.RequestListener
import org.json.JSONObject

internal object Graph {

    private const val URL_GRAPH = "http://graph.voip24h.vn/"

    /**
     * method: POST, GET, PUT, DELETE, HEAD, TRACE, PATCH, OPTIONS
     * bearer: This is access token
     */

    fun sendRequest(
        method: Method,
        endpoint: String,
        bearer: String,
        params: List<Pair<String, Any>>? = null,
        listener: RequestListener? = null
    ) {
        val fuelRequest = Fuel.request(method, "$URL_GRAPH$endpoint", params)
        fuelRequest.authentication().bearer(bearer)
        fuelRequest.response { result ->
            when (result) {
                is Result.Success -> listener?.success(JSONObject(String(result.value)))
                is Result.Failure -> listener?.failed(result.error)
            }
        }
    }
}

inline fun <reified T> JSONObject.toObject(): T? {
    return try {
        var jsonObject =
            ((this["data"] as JSONObject)["response"] as JSONObject)["data"] as JSONObject
        if (jsonObject.has("data")) {
            jsonObject = jsonObject["data"] as JSONObject
        }
        val gson = Gson()
        gson.fromJson(jsonObject.toString(), T::class.java)
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        null
    }
}

inline fun <reified T> JSONObject.toListObject(): List<T>? {
    return try {
        val jsonArray = ((this["data"] as JSONObject)["response"] as JSONObject)["data"]
        val gson = Gson()
        val type = object : TypeToken<List<T>>() {}.type
        gson.fromJson(jsonArray.toString(), type)
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        null
    }
}

fun JSONObject.offset(): Int {
    return try {
        (((this["data"] as JSONObject)["response"] as JSONObject)["meta"] as JSONObject)["offset"].toString()
            .toIntOrNull() ?: 0
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        -1
    }
}

fun JSONObject.total(): Int {
    return try {
        (((this["data"] as JSONObject)["response"] as JSONObject)["meta"] as JSONObject)["total"].toString()
            .toIntOrNull() ?: 0
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        -1
    }
}

fun JSONObject.statusCode(): Int {
    return try {
        ((this["data"] as JSONObject)["response"] as JSONObject)["status"].toString().toIntOrNull()
            ?: 0
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        -1
    }
}

fun JSONObject.message(): String {
    return try {
        ((this["data"] as JSONObject)["response"] as JSONObject)["message"].toString()
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        ""
    }
}

fun JSONObject.limit(): Int {
    return try {
        (((this["data"] as JSONObject)["response"] as JSONObject)["meta"] as JSONObject)["limit"].toString()
            .toIntOrNull() ?: 0
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        -1
    }
}

fun JSONObject.isSort(): String {
    return try {
        (((this["data"] as JSONObject)["response"] as JSONObject)["meta"] as JSONObject)["sort"].toString()
    } catch (e: Exception) {
        Log.e("Graph", e.message.toString())
        ""
    }
}