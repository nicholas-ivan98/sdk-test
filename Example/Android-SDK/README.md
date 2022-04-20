# Android-SDK

Android SDK - VOIP24H

### Tính năng
- Graph:

	- Lấy access token
	
	- Request API từ WebSDK: https://docs-sdk.voip24h.vn/
	
- Callkit (cơ chế SIP): 
	
  - Đăng kí tài khoản sip
		
  - Xử lý gọi đi
		
  - Nhận cuộc gọi đến
		
  - Chuyển cuộc gọi
		
  - Tắt/Mở mic
	 
  - ...

### Yêu cầu
- MinSDK: 23 (Android 6.0)
- Trong app `AndroidManifest`:

  - Cấp quyền ứng dụng
  
    ```
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
    ```
    
  - Trong application thêm thuộc tính:
 
    ```
    <application
        ...
        android:usesCleartextTraffic="true">
    ```

### Cài đặt

Bước 1: Trong folder root project mở terminal nhập dòng lệnh

```
// HTTPS
git submodule add https://github.com/handeskXYZ/Android-SDK.git

Hoặc

// SSH
git submodule add git@github.com:handeskXYZ/Android-SDK.git
```

Bước 2: Trong `settings.gradle`. Thêm
```
include ':Android-SDK:voip24h-sdk'
include ':Android-SDK:linphone-sdk'
```

Bước 3: Trong app `build.gradle`. Thêm dependencies
```
implementation project(path: ':Android-SDK:voip24h-sdk')
```

### Ví dụ
- Graph:
	- Lấy access token trước khi request các api khác
	
  vd: Lấy access token
	```
	GraphManager.getAccessToken(
        "your api_key",
        "your api_secret",
        object : AuthorizationListener {
	        override fun success(oauth: OAuth) {
	            Log.d(TAG, oauth.isToken)
            }
            override fun failed(exception: Exception) {
                Log.d(TAG, exception.message.toString())
            }
        }
    )
	```
   
   - Request api: tham khảo https://docs-sdk.voip24h.vn/
    	
    + Sử dụng function: GraphManager.sendRequest()
    
    + Tham số function:
        + method: Method.POST, Method.GET, Method.PUT,...
        
        + endpoint: "call/find", "call/findone", "phonebook/find",...
        
        + token: access token
        
        + params: tham số để request
        
        + listener: sự kiện trả về
        
    + Kết quả trả về: dạng  jsonObject
    
        + Dạng list: sử dụng jsonObject.toList\<T>\()

        + Dạng object: sử dụng jsonObject.toObject\<T>\()
	
        + Trạng thái request: jsonObject.statusCode(), jsonObject.message(),....
	
        + Lưu ý: T là kiểu dữ liệu tự định nghĩa 
    
    vd: Định nghĩa kiểu dữ liệu
    ```
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
    ```
    
    vd: Lấy lịch sử cuộc gọi 
    ```
    GraphManager.sendRequest(
        Method.POST,
        "call/find",
        accessToken,
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
    ```
    vd: Lấy thông tin cuộc gọi
    ```
    GraphManager.sendRequest(
        Method.POST,
        "call/findone",
        accessToken,
        listOf("callid" to "your call id"),
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
    ```
    vd: Lấy file ghi âm
    ```
    // Model
    data class MediaRecord(
        val ogg: String,
        val wav: String
    )
    
    // Request api
    GraphManager.sendRequest(
        Method.POST,
        "call/media",
        accessToken,
        listOf("callid" to "your call id"),
        object : RequestListener {
            override fun success(jsonObject: JSONObject) {
                val obj = jsonObject.toObject<MediaRecord>()
                Log.d(TAG, obj.wav)
            }

            override fun failed(exception: Exception) {
                Log.d(TAG, exception.message.toString())
            }
        }
    )
    ```
- Callkit:
	
    - Cấp quyền runtime `RECORD_AUDIO` trước khi sử dụng 
	
    - Đăng kí sip account
	
    ```
    val sipConfiguration = SipConfiguration.Builder("ext", "password", "domain")
            .transport(TransportType.Tcp)
            .setMediaEncryption(MediaEncryption.ZRTP)
            .create()

    CallManager.getInstance(this).registerSipAccount(sipConfiguration, object : RegistrationListener {
        override fun onRegistrationStateChange(state: RegistrationState, message: String) {
            Log.d(TAG, state.name + " - " + message)
        }
    })
    ```
	
    - Huỷ đăng ký
  
    ```
    CallManager.getInstance(this).logout()
    ```	
	
    - Chức năng: gọi đi, ngắt máy, chấp nhận cuộc gọi , từ chối cuộc gọi, lắng nghe sự kiện,... 
    
    vd: Gọi đi
    ```
    CallManager.getInstance(this).call("phoneNumber")
    ```
    vd: Ngắt máy
    ```
    CallManager.getInstance(this).hangup("callId")
    ```
    vd: Lắng nghe sự kiện
    ```
    CallManager.getInstance(this).addCallStateListener(object : CallStateListener() {
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
    ```

### License
```
Copyright 2022 VOIP24h

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
