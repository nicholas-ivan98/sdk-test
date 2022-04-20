package com.voip24h.sdk.call.utils

enum class TransportType(val value: String) {
    Udp("Udp"),
    Tcp("Tcp"),
//    Tls("Tls"),
//    Dtls("Dtls"),
    None("None")
}

enum class RegistrationState(val value: String) {
    None("None"),
    Progress("Progress"),
    Ok("Ok"),
    Cleared("Cleared"),
    Failed("Failed")
}

enum class AudioType(val value: String) {
    Unknown("Unknown"),
    Microphone("Microphone"),
    Earpiece("Earpiece"),
    Speaker("Speaker"),
    Bluetooth("Bluetooth"),
    BluetoothA2DP("BluetoothA2DP"),
    Telephony("Telephony"),
    AuxLine("AuxLine"),
    GenericUsb("GenericUsb"),
    Headset("Headset"),
    Headphones("Headphones")
}

enum class ErrorReason(val value: String) {
    None("None"),
    NoResponse("NoResponse"),
    Forbidden("Forbidden"),
    Declined("Declined"),
    NotFound("NotFound"),
    NotAnswered("NotAnswered"),
    Busy("Busy"),
    UnsupportedContent("UnsupportedContent"),
    BadEvent("BadEvent"),
    IOError("IOError"),
    DoNotDisturb("DoNotDisturb"),
    Unauthorized("Unauthorized"),
    NotAcceptable("NotAcceptable"),
    NoMatch("NoMatch"),
    MovedPermanently("MovedPermanently"),
    Gone("Gone"),
    TemporarilyUnavailable("TemporarilyUnavailable"),
    AddressIncomplete("AddressIncomplete"),
    NotImplemented("NotImplemented"),
    BadGateway("BadGateway"),
    SessionIntervalTooSmall("SessionIntervalTooSmall"),
    ServerTimeout("ServerTimeout"),
    Unknown("Unknown"),
    Transferred("Transferred")
}

enum class MediaEncryption(val value: String) {
    None("None"),
    SRTP("SRTP"),
    ZRTP("ZRTP"),
//    DTLS("DTLS")
}