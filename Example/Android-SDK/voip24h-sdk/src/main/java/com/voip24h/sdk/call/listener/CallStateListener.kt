package com.voip24h.sdk.call.listener

import com.voip24h.sdk.call.utils.ErrorReason

abstract class CallStateListener {

    open fun onOutgoingInit() {}

    open fun onOutgoingProgress(callId: String?) {}

    open fun onOutgoingRinging(callId: String?) {}

    open fun onIncomingCall(caller: String?) {}

    open fun onStreamRunning(callId: String?, caller: String?) {}

    open fun onMissed(caller: String, totalMissed: Int) {}

    open fun onEnded() {}

    open fun onReleased() {}

    open fun onPause(callId: String?) {}

    open fun onPauseByRemote() {}

    open fun onResuming(callId: String?) {}

    open fun onError(errorReason: ErrorReason) {}
}