package com.shuhart.moneyedittext

import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.KeyListener
import java.text.NumberFormat
import java.util.*

object MoneyTextUtils {
    @Suppress("DEPRECATION")
    fun buildKeyListener(acceptedChars: CharArray): KeyListener {
        return object : DigitsKeyListener() {
            override fun getAcceptedChars(): CharArray = acceptedChars

            override fun getInputType(): Int = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
    }

    fun buildSupportedCryptoSymbols(currencies: List<String>): CharArray {
        val result = mutableListOf<Char>()
        currencies
                .map { getCurrencyInfo(it).formattedSymbol.toCharArray() }
                .forEach { it.forEach { result.add(it) } }
        return result.distinct().toCharArray()
    }

    private fun extractCurrency(text: String): CurrencyTextInfo {
        val index = text.indexOf("1")
        var formattedSymbol = ""
        return when (index) {
            0 -> {
                (text.length - 1 downTo 0)
                        .takeWhile { !text[it].isDigit() }
                        .forEach { formattedSymbol += text[it] }
                CurrencyTextInfo(position = CurrencyTextInfo.CURRENCY_POSITION_END, formattedSymbol = formattedSymbol.reversed())
            }
            else -> {
                (0 until text.length)
                        .takeWhile { !text[it].isDigit() }
                        .forEach { formattedSymbol += text[it] }
                CurrencyTextInfo(position = CurrencyTextInfo.CURRENCY_POSITION_START, formattedSymbol = formattedSymbol)
            }
        }
    }

    fun getCurrencyInfo(currency: String): CurrencyTextInfo {
        val stub = prettyMoney(1.0, currency, 0)
        return extractCurrency(stub)
    }

    fun prettyMoney(number: Double, currency: String, precision: Int): String {
        val numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        numberFormat.currency = Currency.getInstance(currency.toUpperCase())
        numberFormat.minimumFractionDigits = precision
        numberFormat.maximumFractionDigits = precision
        val text = numberFormat.format(number)
        return replaceRussianRublesToOwn(text)
    }

    private fun replaceRussianRublesToOwn(text: String): String {
        val indexEnUs = text.indexOf("RUB")
        val indexRu = text.indexOf("руб.")
        if (indexEnUs >= 0) {
            return text.replace("RUB", "\u20BD")
        }
        return if (indexRu >= 0) {
            text.replace("руб.", "\u20BD")
        } else text
    }
}

class CurrencyTextInfo(val position: Int, val formattedSymbol: String) {
    companion object {
        val CURRENCY_POSITION_START = 0
        val CURRENCY_POSITION_END = 1
    }
}