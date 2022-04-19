package com.voip24h.sdk.call

import android.content.Context
import com.voip24h.sdk.call.listener.CallStateListener
import com.voip24h.sdk.call.listener.RegistrationListener
import com.voip24h.sdk.call.utils.AudioType
import com.voip24h.sdk.call.utils.SipConfiguration
import com.voip24h.sdk.call.utils.TransportType

class CallManager private constructor(context: Context) {

    private val sipManager by lazy { SipManager.getInstance(context) }

    fun registerSipAccount(sipConfiguration: SipConfiguration, listener: RegistrationListener) =
        sipManager.init(sipConfiguration, listener)

    fun logout() = sipManager.logout()

    fun refreshRegister() = sipManager.refreshRegister()

    fun hangup(callId: String?) {
        sipManager.hangup(callId)
    }

    fun transfer(phoneNumber: String) {
        sipManager.transfer(phoneNumber)
    }

    fun call(phoneNumber: String) {
        sipManager.call(phoneNumber)
    }

    fun decline(callId: String?) = sipManager.decline(callId)

    fun answer() = sipManager.answer()

    fun pause(callId: String?) = sipManager.pause(callId)

    fun resume(callId: String?) = sipManager.resume(callId)

    fun getCallId() = sipManager.getCallId()

    fun setOutputAudioType(type: AudioType) = sipManager.setOutputAudioType(type)

    fun enableMic(isEnable: Boolean) = sipManager.enableMic(isEnable)

    fun isMicEnable(): Boolean = sipManager.isMicEnable()

    fun getAudioType(): AudioType = sipManager.getAudioType()

    fun isSipRegistered(): Boolean = sipManager.isSipRegistered()

    fun getExt(): String = sipManager.getExt()

    fun getDomain(): String = sipManager.getDomain()

    fun getTransport(): TransportType = sipManager.getTransport()

    fun getMissedCalls(): Int = sipManager.getMissedCalls()

    fun addCallStateListener(listener: CallStateListener) = sipManager.addListener(listener)

    fun removeListener(listener: CallStateListener) = sipManager.removeListener(listener)

    companion object {
        private var INSTANCE: CallManager? = null

        fun getInstance(context: Context): CallManager {
            return INSTANCE ?: synchronized(CallManager::class.java) {
                INSTANCE ?: CallManager(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}