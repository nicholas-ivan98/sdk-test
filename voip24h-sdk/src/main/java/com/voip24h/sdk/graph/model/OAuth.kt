package com.voip24h.sdk.graph.model

import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.util.*

data class OAuth(
    @SerializedName("IsToken")
    val isToken: String,
    @SerializedName("Createat")
    val createAt: Date,
    @SerializedName("Expried")
    val expired: Date
) {
    class Deserializer : ResponseDeserializable<OAuth> {
        override fun deserialize(content: String): OAuth? {
            val responseAuth = Gson().fromJson(content, ResponseToken::class.java)
            val stringAuth = JSONObject(responseAuth.data.response["data"] as Map<*, *>).toString()
            return GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(stringAuth, OAuth::class.java)
        }
    }
}

data class ResponseToken(
    val data: DataAuth
) {
    data class DataAuth(
        val status: Int,
        val message: String,
        val response: Map<String, Any>
    )

    fun getToken(): String {
        return (data.response["data"] as? Map<*, *>)?.get("IsToken")?.toString() ?: ""
    }
}
