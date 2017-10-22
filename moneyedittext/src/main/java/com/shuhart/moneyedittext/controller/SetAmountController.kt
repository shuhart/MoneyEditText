package com.shuhart.moneyedittext.controller

import java.text.DecimalFormat

class SetAmountController(private var precision: Int = 2) {
    private val format = "0.${"0".repeat(precision.apply { if (this < 0) precision = 2 })}"
    private val decimalFormat = DecimalFormat(format)
    private val decimalSeparator = "."

    fun amountToString(amount: Double?): String {
        amount ?: return ""

        val sb = StringBuilder()
        val formattedAmount = format(amount)
        val decimalPart = formatDecimalPart(formattedAmount)
        sb.append(decimalPart)
        sb.insert(0, decimalSeparator)

        val integerPart = getIntegerPart(formattedAmount)
        sb.insert(0, integerPart)

        return sb.toString()
    }

    private fun getIntegerPart(formattedAmount: String): String =
            formattedAmount.substring(0, formattedAmount.length - precision - 1)

    private fun formatDecimalPart(formattedAmount: String): String {
        var decimalPart = getDecimalPart(formattedAmount)
        if (decimalPart == "00") return decimalPart
        decimalPart = decimalPart.dropLastWhile { it == '0' }
        if (decimalPart.isEmpty()) {
            decimalPart = "00"
        } else if (decimalPart.length == 1) {
            decimalPart += "0"
        }
        return decimalPart
    }

    private fun getDecimalPart(formattedAmount: String): String =
            formattedAmount.substring(formattedAmount.length - precision, formattedAmount.length)

    private fun format(amount: Double): String = decimalFormat.format(amount)
}