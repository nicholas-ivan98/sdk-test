package com.news.testsdk

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.voip24h.sdk.call.CallManager
import com.voip24h.sdk.call.listener.CallStateListener
import com.voip24h.sdk.call.listener.RegistrationListener
import com.voip24h.sdk.call.utils.*
import com.voip24h.sdk.graph.GraphManager
import com.voip24h.sdk.graph.listener.AuthorizationListener
import com.voip24h.sdk.graph.listener.RequestListener
import com.voip24h.sdk.graph.model.OAuth
import com.voip24h.sdk.graph.toListObject
import com.voip24h.sdk.graph.toObject
import com.voip24h.sdk.graph.utils.Method
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val callManager by lazy { CallManager.getInstance(this) }
    private var token: String = ""
    private val TAG = this::class.java.name

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1024)
            return
        }

        GraphManager.getAccessToken(
            "9ec35fd47a4a9bfb19ff0f33a136413ed50b2834",
            "9d7a1902cbbb589f7f14589ac9deb1fde839e2d0",
            object : AuthorizationListener {
                override fun success(oauth: OAuth) {
                    Log.d(TAG, oauth.isToken)
//                    sendRequest(oauth.isToken)
//                    sendRequestObject(oauth.isToken)
//                    token = oauth.isToken
                }

                override fun failed(exception: Exception) {
                    Log.d(TAG, exception.message.toString())
                }
            }
        )

        eventListener()
    }

    private fun call() {
        callManager.call("0973566691")
    }

    private fun hangup() {
        val callId = callManager.getCallId()
        callManager.hangup(callId)
    }

    private fun decline() {
        val callId = callManager.getCallId()
        callManager.decline(callId)
    }

    private fun answer() {
        callManager.answer()
    }

    private fun pause() {
        val callId = callManager.getCallId()
        callManager.pause(callId)
    }

    private fun resume() {
        val callId = callManager.getCallId()
        callManager.resume(callId)
    }

    private fun transfer() {
        callManager.transfer("phoneNumber")
    }

    private fun sendRequest(token: String) {
        GraphManager.sendRequest(
            Method.POST,
            "call/find",
            token,
            listOf("date_start" to "2022-01-01 00:00:00", "date_end" to "2022-04-15 23:59:50"),
            object : RequestListener {
                override fun success(jsonObject: JSONObject) {
                    val listObject = jsonObject.toListObject<CallHistory>()
                    Log.d(TAG, listObject.toString())
                }

                override fun failed(exception: Exception) {
                    Log.d(TAG, exception.message.toString())
                }
            }
        )
    }

    private fun sendRequestObject(token: String) {
        GraphManager.sendRequest(
            Method.POST,
            "call/findone",
            token,
            listOf("callid" to "1650005709.518"),
            object : RequestListener {
                override fun success(jsonObject: JSONObject) {
                    val obj = jsonObject.toObject<CallHistory>()
                    Log.d(TAG, obj.toString())
                }

                override fun failed(exception: Exception) {
                    Log.d(TAG, exception.message.toString())
                }
            }
        )
    }

    private fun eventListener() {
        findViewById<Button>(R.id.call).setOnClickListener {
            call()
        }

        findViewById<Button>(R.id.hangup).setOnClickListener {
            hangup()
        }

        findViewById<Button>(R.id.decline).setOnClickListener {
            decline()
        }

        findViewById<Button>(R.id.answer).setOnClickListener {
            answer()
        }

        findViewById<Button>(R.id.pause).setOnClickListener {
            pause()
        }

        findViewById<Button>(R.id.resume).setOnClickListener {
            resume()
        }

        findViewById<Button>(R.id.enableMic).setOnClickListener {
            callManager.enableMic(!callManager.isMicEnable())
        }

        findViewById<Button>(R.id.toggleSpeaker).setOnClickListener {
            if(callManager.getAudioType() == AudioType.Earpiece) {
                callManager.setOutputAudioType(AudioType.Speaker)
            } else {
                callManager.setOutputAudioType(AudioType.Earpiece)
            }
        }

        findViewById<Button>(R.id.transfer).setOnClickListener {
            transfer()
        }

        findViewById<Button>(R.id.login).setOnClickListener {
            login()
        }

        findViewById<Button>(R.id.logout).setOnClickListener {
            logout()
        }
    }

    private fun login() {
        val sipConfiguration = SipConfiguration.Builder("102", "voip24h.vn", "14.225.251.72")
            .isIpv6Enable(true)
            .isKeepAlive(true)
            .transport(TransportType.Tcp)
            .setMediaEncryption(MediaEncryption.ZRTP)
            .create()

        callManager.registerSipAccount(sipConfiguration, object : RegistrationListener {
            override fun onRegistrationStateChange(state: RegistrationState, message: String) {
                Log.d(TAG, state.name + " - " + message)
                Log.d(TAG, callManager.isSipRegistered().toString())
                Log.d(TAG, callManager.getExt())
                Log.d(TAG, callManager.getDomain())
                Log.d(TAG, callManager.getTransport().name)
            }
        })

        callManager.addCallStateListener(object : CallStateListener() {

            override fun onOutgoingInit() {
                Log.d(TAG, "onOutgoingInit")
            }

            override fun onOutgoingProgress(callId: String?) {
                Log.d(TAG, "onOutgoingProgress: $callId")
            }

            override fun onOutgoingRinging(callId: String?) {
                Log.d(TAG, "onOutgoingRinging: $callId")
            }

            override fun onIncomingCall(caller: String?) {
                Log.d(TAG, "onIncomingCall: $caller")
            }

            override fun onPause(callId: String?) {
                Log.d(TAG, "onPause: $callId")
            }

            override fun onEnded() {
                Log.d(TAG, "onEnded")
            }

            override fun onError(errorReason: ErrorReason) {
                Log.d(TAG, "onError: ${errorReason.name}")
            }

            override fun onPauseByRemote() {
                Log.d(TAG, "onPauseByRemote")
            }

            override fun onStreamRunning(callId: String?, caller: String?) {
                Log.d(TAG, "onStreamRunning: $callId - $caller")
            }

            override fun onReleased() {
                Log.d(TAG, "onReleased")
            }

            override fun onResuming(callId: String?) {
                Log.d(TAG, "onResuming: $callId")
            }

            override fun onMissed(caller: String, totalMissed: Int) {
                Log.d(TAG, "onMissed: $caller - $totalMissed")
            }
        })
    }

    private fun logout() {
        callManager.logout()
        Log.d(TAG, callManager.isSipRegistered().toString())
        Log.d(TAG, callManager.getExt())
        Log.d(TAG, callManager.getDomain())
        Log.d(TAG, callManager.getTransport().name)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1024 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }
}