package com.voip24h.sdk.call.listener

import com.voip24h.sdk.call.utils.RegistrationState

interface RegistrationListener {
    fun onRegistrationStateChange(state: RegistrationState, message: String)
}