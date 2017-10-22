package com.shuhart.moneyedittext

import android.text.Editable
import android.text.Selection
import android.text.SpannableStringBuilder
import android.text.Spanned
import com.shuhart.moneyedittext.span.DecoratorSpan
import com.shuhart.moneyedittext.span.ReplaceSpan
import java.text.DecimalFormatSymbols
import java.util.*

interface MoneyDecorator {
    fun decor(editable: Editable, text: String, selection: Int)
    fun getDecimalFormatSymbol(): String
    fun getThousandFormatSymbol(): String
    var currency: String
}

open class DefaultMoneyDecorator(override var currency: String) : MoneyDecorator {
    private val decimalFormatSymbols = DecimalFormatSymbols(Locale.getDefault())

    open var selection: Int = 0

    override final fun decor(editable: Editable, text: String, selection: Int) {
        if (text.isEmpty()) {
            editable.replace(0, editable.length, "")
            return
        }

        this.selection = selection
        val mutableSpannableStringBuilder = SpannableStringBuilder(text)
        decorNumber(mutableSpannableStringBuilder)
        decor(mutableSpannableStringBuilder)

        editable.replace(0, editable.length, mutableSpannableStringBuilder)

        if (this.selection > editable.length) {
            this.selection = editable.length
        }
        Selection.setSelection(editable, this.selection)
    }

    open fun decor(mutableSpannableStringBuilder: SpannableStringBuilder) {
        val currencyInfo = MoneyTextUtils.getCurrencyInfo(currency)
        when (currencyInfo.position) {
            CurrencyTextInfo.CURRENCY_POSITION_START -> {
                mutableSpannableStringBuilder.insert(0, currencyInfo.formattedSymbol)
                selection += currencyInfo.formattedSymbol.length
            }
            else -> mutableSpannableStringBuilder.insert(mutableSpannableStringBuilder.length, currencyInfo.formattedSymbol)
        }
    }

    private fun decorNumber(mutableSpannableStringBuilder: SpannableStringBuilder) {
        val decimalSeparatorIndex = mutableSpannableStringBuilder.indexOf(".")

        if (decimalSeparatorIndex == -1) {
            if (mutableSpannableStringBuilder.length <= 3) return

            mutableSpannableStringBuilder.forEachIndexed { index, _ ->
                val invertIndex = mutableSpannableStringBuilder.length - index - 1
                if ((invertIndex + 1).rem(3) == 0) {
                    val realIndex = mutableSpannableStringBuilder.length - invertIndex - 1
                    if (realIndex != 0) {
                        mutableSpannableStringBuilder.setSpan(DecoratorSpan(before = getThousandFormatSymbol()),
                                realIndex, realIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            return
        }

        mutableSpannableStringBuilder.setSpan(ReplaceSpan(getDecimalFormatSymbol()), decimalSeparatorIndex,
                decimalSeparatorIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val reversed = mutableSpannableStringBuilder.reversed()
        var separatorPassed = false
        var separatorIndex = 0

        reversed.forEachIndexed { index, c ->
            if (!separatorPassed) {
                if (c.toString() == ".") {
                    separatorIndex = index
                    separatorPassed = true
                }
            } else {
                val invertIndex = index - separatorIndex - 1
                if ((invertIndex + 1).rem(3) == 0) {
                    val realInvertIndex = invertIndex + separatorIndex + 1
                    val realIndex = mutableSpannableStringBuilder.length - realInvertIndex - 1
                    if (realIndex != 0) {
                        mutableSpannableStringBuilder.setSpan(DecoratorSpan(before = getThousandFormatSymbol()),
                                realIndex, realIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
        }
    }

    override fun getDecimalFormatSymbol(): String = decimalFormatSymbols.decimalSeparator.toString()

    override fun getThousandFormatSymbol(): String = decimalFormatSymbols.groupingSeparator.toString()
}