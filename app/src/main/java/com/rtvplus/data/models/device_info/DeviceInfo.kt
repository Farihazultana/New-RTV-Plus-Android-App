package com.rtvplus.data.models.device_info

data class DeviceInfo(
    var deviceId: String = "",
    var softwareVersion: String = "",
    var simSerialNumber: String = "",
    var operator: String = "",
    var operatorName: String = "",
    var brand: String = "",
    var model: String = "",
    var release: String = "",
    var sdkVersion: Int = 0,
    var versionCode: Int = 0
)
