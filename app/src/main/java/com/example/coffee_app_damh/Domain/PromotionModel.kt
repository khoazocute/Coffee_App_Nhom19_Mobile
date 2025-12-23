package com.example.coffee_app_damh.Domain

import java.io.Serializable

data class PromotionModel(
    var code: String = "",
    var discountPercent: Int = 0, // Ví dụ: 10, 20, 50
    var description: String = ""
) : Serializable
