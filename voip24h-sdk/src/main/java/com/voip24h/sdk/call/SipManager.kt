package com.voip24h.sdk.call

import android.content.Context
import android.util.Log
import com.voip24h.sdk.call.listener.CallStateListener
import com.voip24h.sdk.call.listener.RegistrationListener
import com.voip24h.sdk.call.utils.AudioType
import com.voip24h.sdk.call.utils.ErrorReason
import com.voip24h.sdk.call.utils.SipConfiguration
import org.linphone.core.*
import com.voip24h.sdk.call.utils.RegistrationState as RegisterState
import com.voip24h.sdk.call.utils.TransportType as Transport

internal class SipManager private constructor(context: Context) {

    private var core: Core
    private var registerListener: RegistrationListener? = null
    private val listeners = mutableListOf<CallStateListener>()
    private val TAG = this::class.java.name

    private val coreListener = object : CoreListenerStub() {
        override fun onRegistrationStateChanged(
            core: Core,
            proxyConfig: ProxyConfig,
            state: RegistrationState?,
            message: String
        ) {
            if (state == null) return
            registerListener?.onRegistrationStateChange(
                RegisterState.values()[state.toInt()],
                message
            )
        }

        override fun onCallLogUpdated(core: Core, callLog: CallLog) {
            super.onCallLogUpdated(core, callLog)
            Log.d(TAG, "CallLog: " + callLog.errorInfo?.reason?.name)
            Log.d(TAG, "CallLog: " + callLog.errorInfo?.subErrorInfo?.subErrorInfo?.reason?.name)
            Log.d(TAG, "CallLog: " + callLog.status.name)
        }

        override fun onCallCreated(core: Core, call: Call) {
            super.onCallCreated(core, call)
            Log.d(TAG, "onCallCreated: " + call.callLog.toStr())
        }

        override fun onDtmfReceived(core: Core, call: Call, dtmf: Int) {
            super.onDtmfReceived(core, call, dtmf)
            Log.d(TAG, "onDtmfReceived: " + call.callLog.toStr())
        }

        override fun onInfoReceived(core: Core, call: Call, message: InfoMessage) {
            super.onInfoReceived(core, call, message)
            Log.d(TAG, "onInfoReceived: $message")
        }

        override fun onMessageReceived(core: Core, chatRoom: ChatRoom, message: ChatMessage) {
            super.onMessageReceived(core, chatRoom, message)
            Log.d(TAG, "onMessageReceived: $message")
        }

        override fun onTransferStateChanged(core: Core, transfered: Call, callState: Call.State?) {
            super.onTransferStateChanged(core, transfered, callState)
            Log.d(TAG, "onTransferStateChanged: " + callState?.name)
        }

        override fun onCallStateChanged(
            core: Core,
            call: Call,
            state: Call.State?,
            message: String
        ) {
            when (state) {
                Call.State.Idle -> Log.d(TAG, "onCallStateChanged: Idle")
                Call.State.IncomingReceived -> {
                    Log.d(TAG, "onCallStateChanged: IncomingReceived")
                    val caller = call.remoteAddress.username
                    this@SipManager.listeners.forEach { it.onIncomingCall(caller) }
                }
                Call.State.PushIncomingReceived -> Log.d(
                    TAG,
                    "onCallStateChanged: PushIncomingReceived"
                )
                Call.State.OutgoingInit -> {
                    Log.d(TAG, "onCallStateChanged: OutgoingInit")
                    this@SipManager.listeners.forEach { it.onOutgoingInit() }
                }
                Call.State.OutgoingProgress -> {
                    Log.d(TAG, "onCallStateChanged: OutgoingProgress")
                    val callId = call.callLog.callId
                    this@SipManager.listeners.forEach { it.onOutgoingProgress(callId) }
                }
                Call.State.OutgoingRinging -> {
                    Log.d(TAG, "onCallStateChanged: OutgoingRinging")
                    val callId = call.callLog.callId
                    this@SipManager.listeners.forEach { it.onOutgoingRinging(callId) }
                }
                Call.State.OutgoingEarlyMedia -> Log.d(
                    TAG,
                    "onCallStateChanged: OutgoingEarlyMedia"
                )
                Call.State.Connected -> Log.d(
                    TAG,
                    "onCallStateChanged: Connected - ${call.remoteAddress.displayName}"
                )
                Call.State.StreamsRunning -> {
                    Log.d(
                        TAG,
                        "onCallStateChanged: StreamsRunning - ${call.remoteAddress.username}"
                    )
                    val caller = call.remoteAddress.username
                    val callId = call.callLog.callId
                    this@SipManager.listeners.forEach { it.onStreamRunning(callId, caller) }
                }
                Call.State.Pausing -> Log.d(TAG, "onCallStateChanged: Pausing")
                Call.State.Paused -> {
                    Log.d(TAG, "onCallStateChanged: Paused")
                    val callId = call.callLog.callId
                    this@SipManager.listeners.forEach { it.onPause(callId) }
                }
                Call.State.Resuming -> {
                    Log.d(TAG, "onCallStateChanged: Resuming")
                    val callId = call.callLog.callId
                    this@SipManager.listeners.forEach { it.onResuming(callId) }
                }
                Call.State.Referred -> Log.d(TAG, "onCallStateChanged: Referred")
                Call.State.Error -> {
                    Log.d(TAG, "onCallStateChanged: Error")
                    this@SipManager.listeners.forEach { it.onError(ErrorReason.valueOf(call.errorInfo.reason.name)) }
                }
                Call.State.End -> {
                    Log.d(TAG, "onCallStateChanged: OutgoingEarlyMedia")
                    this@SipManager.listeners.forEach { it.onEnded() }
                }
                Call.State.PausedByRemote -> {
                    Log.d(TAG, "onCallStateChanged: PausedByRemote")
                    this@SipManager.listeners.forEach { it.onPauseByRemote() }
                }
                Call.State.UpdatedByRemote -> {
                    Log.d(TAG, "onCallStateChanged: UpdatedByRemote")
                }
                Call.State.IncomingEarlyMedia -> Log.d(TAG, "onCallStateChanged: IncomingEarlyMedia")
                Call.State.Updating -> Log.d(TAG, "onCallStateChanged: Updating")
                Call.State.Released -> {
                    if (isMissed(call.callLog)) {
                        Log.d(TAG, "onCallStateChanged: Missed")
                        val caller = call.remoteAddress.username ?: ""
                        val totalMissed = core.missedCallsCount
                        this@SipManager.listeners.forEach { it.onMissed(caller, totalMissed) }
                    } else {
                        Log.d(TAG, "onCallStateChanged: Released")
                        this@SipManager.listeners.forEach { it.onReleased() }
                    }
                }
                Call.State.EarlyUpdatedByRemote -> Log.d(TAG, "onCallStateChanged: EarlyUpdatedByRemote")
                Call.State.EarlyUpdating -> Log.d(TAG, "onCallStateChanged: EarlyUpdating")
                null -> Log.d(TAG, "onCallStateChanged: null")
            }
        }
    }

    init {
        val factory = Factory.instance()
        core = factory.createCore(null, null, context)
    }

    fun init(sipConfiguration: SipConfiguration, listener: RegistrationListener) {
        this.registerListener = listener
        core.isNetworkReachable = true
        core.isKeepAliveEnabled = sipConfiguration.isKeepAlive
        core.isIpv6Enabled = sipConfiguration.isIpv6Enable
        core.mediaEncryption = MediaEncryption.valueOf(sipConfiguration.mediaEncryption.value)
        identity(
            sipConfiguration.ext,
            sipConfiguration.password,
            sipConfiguration.domain,
            sipConfiguration.transport
        )
    }

    private fun identity(ext: String, password: String, domain: String, transport: Transport) {
        val authInfo =
            Factory.instance().createAuthInfo(ext, null, password, null, null, domain, null)
        val params = core.createAccountParams()
        val identity = Factory.instance().createAddress("sip:$ext@$domain")
        params.identityAddress = identity

        val address = Factory.instance().createAddress("sip:$domain")
        address?.transport = TransportType.valueOf(transport.value)
        params.serverAddress = address
        params.isRegisterEnabled = true

        val account = core.createAccount(params)
        core.addAuthInfo(authInfo)
        core.addAccount(account)
        core.defaultAccount = account
        core.addListener(coreListener)
        core.start()
    }

    fun answer() {
        val call = core.currentCall ?: core.calls.firstOrNull()
        if (call == null) {
            Log.d(TAG, "Current call is null")
            return
        }
        call.accept()
    }

    fun decline(callId: String?) {
        val call =
            callId?.let { core.getCallByCallid(it) } ?: core.currentCall ?: core.calls.firstOrNull()
        if (call == null) {
            Log.d(TAG, "Current call is null")
            return
        }
        call.terminate()
    }

    fun setOutputAudioType(type: AudioType) {
        core.outputAudioDevice = core.audioDevices.find { it.type.name == type.value }
    }

    fun pause(callId: String?) {
        val call =
            callId?.let { core.getCallByCallid(it) } ?: core.currentCall ?: core.calls.firstOrNull()
        if (call == null) {
            Log.d(TAG, "Current call is null")
            return
        }
        call.pause()
    }

    fun resume(callId: String?) {
        val call =
            callId?.let { core.getCallByCallid(it) } ?: core.currentCall ?: core.calls.firstOrNull()
        if (call == null) {
            Log.d(TAG, "Current call is null")
            return
        }
        call.resume()
    }

    fun transfer(phone: String) {
        val currentCall = core.currentCall ?: core.calls.firstOrNull()
        if (currentCall == null) {
            Log.d(TAG, "Couldn't find a call to transfer")
        } else {
            val address = core.interpretUrl(phone)
            if (address != null) {
                Log.d(TAG, "Transferring current call to $phone")
                currentCall.transferTo(address)
            }
        }
    }

    fun call(phone: String) {
        val address = core.interpretUrl(phone)
        if(!isSipRegistered()) {
            Log.d(TAG, "Sip account is not register")
            return
        }
        if (address == null) {
            Log.d(TAG, "Address is null")
            return
        }
        val params = core.createCallParams(null)
        params?.mediaEncryption = core.mediaEncryption
        params?.also {
            core.inviteAddressWithParams(address, it)
        } ?: kotlin.run {
            core.inviteAddress(address)
        }
    }

    fun hangup(callId: String?) {
        val call = callId?.let { core.getCallByCallid(it) } ?: core.currentCall ?: core.calls.firstOrNull()
        if (call == null) {
            Log.d(TAG, "Current call is null")
            return
        }
        call.terminate()
    }

    fun getCallId(): String? {
        return core.currentCall?.callLog?.callId
    }

    fun enableMic(isEnable: Boolean) {
        core.isMicEnabled = isEnable
    }

    fun getMissedCalls(): Int {
        return core.missedCallsCount
    }

    fun refreshRegister() {
        core.refreshRegisters()
    }

    fun logout() {
        core.clearAllAuthInfo()
        core.clearAccounts()
        core.removeListener(coreListener)
    }

    fun addListener(listener: CallStateListener) {
        this.listeners.add(listener)
    }

    fun removeListener(listener: CallStateListener) {
        if (listener in listeners) {
            listeners.remove(listener)
        }
    }

    fun isMicEnable(): Boolean = core.isMicEnabled

    fun getAudioType(): AudioType = AudioType.values()[core.outputAudioDevice?.type?.ordinal ?: 0]

    fun getExt(): String = core.defaultAccount?.contactAddress?.username ?: ""

    fun getDomain(): String = core.defaultAccount?.params?.serverAddress?.domain ?: ""

    fun getTransport() =
        core.defaultAccount?.params?.transport?.let { Transport.valueOf(it.name) } ?: Transport.None

    fun isSipRegistered(): Boolean = core.defaultAccount?.state == RegistrationState.Ok

    private fun isMissed(callLog: CallLog): Boolean {
        return (callLog.dir == Call.Dir.Incoming && (callLog.status == Call.Status.Missed || callLog.status == Call.Status.Aborted || callLog.status == Call.Status.EarlyAborted))
    }

    companion object {
        private var INSTANCE: SipManager? = null

        fun getInstance(context: Context): SipManager {
            return INSTANCE ?: synchronized(SipManager::class.java) {
                INSTANCE ?: SipManager(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}