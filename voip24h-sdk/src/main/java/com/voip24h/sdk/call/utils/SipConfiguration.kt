package com.voip24h.sdk.call.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager

class SipConfiguration private constructor(builder: Builder) {

    var ext: String = ""
    var password: String = ""
    var domain: String = ""

    var transport: TransportType = TransportType.Tcp
    var isKeepAlive: Boolean = true
    var isIpv6Enable: Boolean = true
    var mediaEncryption: MediaEncryption = MediaEncryption.None

    init {
        this.ext = builder.ext
        this.password = builder.password
        this.domain = builder.domain

        this.transport = builder.transport
        this.isKeepAlive = builder.isKeepAlive
        this.isIpv6Enable = builder.isIpv6Enable
        this.mediaEncryption = builder.mediaEncryption
    }

    class Builder constructor(
        val ext: String,
        val password: String,
        val domain: String
    ) {

        var transport: TransportType = TransportType.Tcp
        var isKeepAlive: Boolean = true
        var isIpv6Enable: Boolean = true
        var mediaEncryption: MediaEncryption = MediaEncryption.None

        fun transport(transportType: TransportType): Builder {
            this.transport = transportType
            return this
        }

        fun isKeepAlive(isEnable: Boolean): Builder {
            this.isKeepAlive = isEnable
            return this
        }

        fun isIpv6Enable(isEnable: Boolean): Builder {
            this.isIpv6Enable = isEnable
            return this
        }

        fun setMediaEncryption(mediaEncryption: MediaEncryption): Builder {
            this.mediaEncryption = mediaEncryption
            return this
        }

        fun create(): SipConfiguration {
            return SipConfiguration(this)
        }
    }
}