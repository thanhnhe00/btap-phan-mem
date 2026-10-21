package com.example.coursemanagement.util

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val vi = Locale("vi", "VN")
    fun price(amount: Double): String = NumberFormat.getCurrencyInstance(vi).format(amount)
    fun hours(hours: Double): String = NumberFormat.getNumberInstance(vi).format(hours) + " giờ"
}
