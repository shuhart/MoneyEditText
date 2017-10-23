package com.shuhart.moneyedittext

import android.content.Context
import android.text.Editable
import android.util.AttributeSet

open class MoneyEditText : LocaleMoneyEditText {
    var currency = "USD"
        set(value) {
            if (field == value) return
            field = value
            decorator.currency = currency
            if (!doNotChangeKeyListener) {
                updateKeyListener(field)
            }
            update()
        }

    private var doNotChangeKeyListener: Boolean = false
    private val decorator = DefaultMoneyDecorator(currency = currency)
    private var onValueChangedLambda: ((Double?, String?) -> Unit)? = null

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        updateKeyListener(currency)
    }

    private fun updateKeyListener(currency: String) {
        keyListener = MoneyTextUtils.buildKeyListener(
                MoneyTextUtils.buildSupportedCryptoSymbols(listOf(currency))
                        .plus(supportedSymbols))
    }

    fun setOnValueChangedListener(lambda: ((Double?, String?) -> Unit)?) {
        onValueChangedLambda = lambda
    }

    override fun onValueChanged(amount: Double?) {
        super.onValueChanged(amount)
        onValueChangedLambda?.invoke(getAmount(), currency)
    }

    override fun decor(editable: Editable, text: String, selection: Int) {
        decorator.decor(editable, text, selection)
    }
}