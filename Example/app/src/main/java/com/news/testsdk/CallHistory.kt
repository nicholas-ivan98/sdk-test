package com.news.testsdk

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CallHistory(
    @SerializedName("calldate")
    var callDate: String,
    var caller: String,
    var callee: String,
    var did: String,
    var extension: String,
    var type: String,
    var status: String,
    @SerializedName("callid")
    var callid: String,
    var duration: Long,
    @SerializedName("billsec")
    var billSec: Int
) : Serializable