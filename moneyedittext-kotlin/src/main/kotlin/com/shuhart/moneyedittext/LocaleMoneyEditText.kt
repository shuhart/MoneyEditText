package com.shuhart.moneyedittext

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import com.shuhart.moneyedittext.controller.*

abstract class LocaleMoneyEditText : EditText {
    protected val supportedSymbols = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.')

    protected open val setAmountController = SetAmountController()
    protected open val inputController = InputController(decimalSeparator = '.',
            maxDecimalNumbers = 2,
            maxIntegerNumbers = 10)
    protected open val pasteController = PasteController(vendor = Build.MANUFACTURER,
            decimalSeparator = '.',
            maxDecimalNumbers = 2,
            maxIntegerNumbers = 10)

    protected val cleaner = CleanerImpl()
    private val getAmountController = GetAmountController()
    private val deleteController = DeleteController()
    private val mainWatcher = MoneyTextWatcher()
    private val listeners = mutableListOf<((Double?) -> Unit)>()

    private enum class EditTextOperation {
        INPUT,
        DELETE,
        PASTE,
        UNKNOWN
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        super.addTextChangedListener(mainWatcher)
        keyListener = MoneyTextUtils.buildKeyListener(supportedSymbols)
    }

    fun addOnValueChangeListener(listener: (Double?) -> Unit) {
        listeners.add(listener)
    }

    fun removeValueChangeListener(listener: (Double?) -> Unit) {
        listeners.remove(listener)
    }

    override fun isSuggestionsEnabled(): Boolean = false

    fun setAmount(amount: Double?, silently: Boolean = false) {
        val str = setAmountController.amountToString(amount)
        mainWatcher.setAmount(text, str)
        if (!silently) {
            onValueChanged(amount)
        }
    }

    protected open fun onValueChanged(amount: Double?) {
        listeners.forEach { it.invoke(amount) }
    }

    fun getAmount(): Double? {
        val clearedData = cleaner.clean(text.toString(), Selection.getSelectionStart(text))
        return getAmountController.amountToDouble(clearedData.first)
    }

    protected open fun update() {
        mainWatcher.update(text)
    }

    inner class MoneyTextWatcher : TextWatcher {
        private var selfChange = false
        private var beforeText: String = ""
        private var beforeSelection: Int = 0
        private var previousAmount: Double? = null
        private var savedError: CharSequence? = null

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            if (selfChange) return
            savedError = error
            val beforeData = cleaner.clean(s.toString(), Selection.getSelectionStart(s))
            beforeText = beforeData.first
            beforeSelection = beforeData.second
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (selfChange) return
            val operationType = getOperationType(before, count)

            if (operationType == EditTextOperation.INPUT) {
                handleInput(s, start, count)
            }
            if (operationType == EditTextOperation.DELETE) {
                handleDelete(s)
            }

            if (operationType == EditTextOperation.PASTE) {
                handlePaste(s)
            }
        }

        override fun afterTextChanged(s: Editable) {
            if (selfChange) {
                return
            }
            val amount = getAmount()
            if (amount == previousAmount) {
                if (!savedError.isNullOrEmpty()) {
                    error = savedError
                }
                return
            }
            previousAmount = amount
            onValueChanged(amount)
        }

        private fun getOperationType(before: Int, count: Int): EditTextOperation {
            if (count == 1) {
                return EditTextOperation.INPUT
            }
            if (count > 1) {
                return EditTextOperation.PASTE
            }
            if (before >= 1) {
                return EditTextOperation.DELETE
            }
            return EditTextOperation.UNKNOWN
        }

        private fun handleInput(s: CharSequence, start: Int, count: Int) {
            val (currentText, currentSelection) = cleaner.clean(s.toString(), Selection.getSelectionStart(s))
            val data = inputController.handleInput(currentText, currentSelection, beforeText, beforeSelection, s, start, count)
            selfChange = true
            if (s is Editable) {
                decor(s, data.first, data.second)
            }

            selfChange = false

        }

        private fun handleDelete(s: CharSequence) {
            val (realText, realSelection) = cleaner.clean(s.toString(), Selection.getSelectionStart(s))
            val data = deleteController.handDelete(s, beforeText, beforeSelection, realText, realSelection)

            selfChange = true
            if (s is Editable) {
                decor(s, data.first, data.second)
            }
            selfChange = false
        }

        private fun handlePaste(cs: CharSequence) {
            selfChange = true
            val (text, selection) = pasteController.paste(beforeText, beforeSelection,
                    cs.toString(), Selection.getSelectionStart(cs))
            if (cs is Editable) {
                decor(cs, text, selection)
            }
            selfChange = false
        }

        fun update(e: Editable) {
            selfChange = true
            val (currentText, currentSelection) = cleaner.clean(e.toString(),
                    Selection.getSelectionStart(e))
            decor(e, currentText, currentSelection)
            selfChange = false
        }

        fun setAmount(e: Editable, text: String) {
            selfChange = true
            decor(e, text, text.length)
            selfChange = false
        }
    }

    open protected fun decor(editable: Editable, text: String, selection: Int) {}
}