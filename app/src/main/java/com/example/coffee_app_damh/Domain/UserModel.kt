package com.example.coffee_app_damh.Domain

data class UserModel(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "user" // user hoặc admin
) {
    // Constructor rỗng bắt buộc cho Firebase
    constructor() : this("", "", "", "", "user")
}
