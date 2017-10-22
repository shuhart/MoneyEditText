package com.shuhart.moneyedittext.controller

class GetAmountController {
    fun amountToDouble(text: String): Double? = text.toDoubleOrNull()
}