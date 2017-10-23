package com.shuhart.moneyedittext.controller

class PasteController(
        private val maxIntegerNumbers: Int = 10,
        private val maxDecimalNumbers: Int = 2,
        private val decimalSeparator: Char,
        val vendor: String? = null) {

    private val isSamsung = "samsung".equals(vendor, true)

    fun paste(beforeText: String, beforeSelection: Int,
              currentText: String, currentSelection: Int): Pair<String, Int> {

        var (newTextBuilder, newSelection, isSeparatorAdded) =
                if (isSamsung && beforeText.count { it == decimalSeparator } == 1 &&
                        currentText.count { it == decimalSeparator } == 2) {
                    // Fix samsung autocorrect
                    clearText(beforeText, beforeSelection)
                } else clearText(currentText, currentSelection)


        val integerPart: String
        var decimalPart = ""

        if (isSeparatorAdded) {
            val separatorIndex = newTextBuilder.indexOf(decimalSeparator)
            if (separatorIndex != -1) {
                integerPart = newTextBuilder.substring(0, separatorIndex)
                decimalPart = newTextBuilder.substring(separatorIndex + 1, newTextBuilder.length)
            } else integerPart = newTextBuilder.toString()
        } else {
            integerPart = newTextBuilder.toString()
        }

        var result = handleIntegerPart(integerPart, newTextBuilder, newSelection)
        result = handleDecimalPart(decimalPart, result.first, result.second)
        newTextBuilder = result.first
        newSelection = result.second

        return Pair(newTextBuilder.toString(), newSelection)
    }

    private fun clearText(currentText: String, currentSelection: Int): Triple<StringBuilder, Int, Boolean> {
        val newTextBuilder = StringBuilder()
        var newSelection = currentSelection

        var isSeparatorAdded = false
        for (i in currentText.length - 1 downTo 0) {
            val c = currentText[i]
            var symbolIgnored = false

            if (isDecimalSeparator(c)) {
                if (isSeparatorAdded) {
                    symbolIgnored = true
                } else {
                    newTextBuilder.insert(0, c)
                    isSeparatorAdded = true
                }
            } else if (c.isDigit()) {
                newTextBuilder.insert(0, c)
            } else {
                symbolIgnored = true
            }

            if (symbolIgnored && newSelection > i) {
                newSelection--
            }
        }
        return Triple(newTextBuilder, newSelection, isSeparatorAdded)
    }

    private fun handleIntegerPart(finalIntegerPart: String, newTextBuilder: StringBuilder,
                                  selection: Int): Pair<StringBuilder, Int> {
        var newSelection = selection
        val integerPartBuilder = StringBuilder(finalIntegerPart)
        if (finalIntegerPart.isEmpty()) {
            newTextBuilder.insert(0, "0")
            if (newSelection > 0) {
                newSelection++
            }
            return Pair(newTextBuilder, newSelection)
        }
        while (integerPartBuilder.startsWith('0')) {
            integerPartBuilder.deleteCharAt(0)
            newTextBuilder.deleteCharAt(0)
            if (newSelection > 0) {
                newSelection--
            }
        }

        if (integerPartBuilder.isEmpty()) {
            newTextBuilder.insert(0, "0")
            if (newSelection > 0) {
                newSelection++
            }
            return Pair(newTextBuilder, newSelection)
        }

        if (integerPartBuilder.length > maxIntegerNumbers) {
            newTextBuilder.delete(maxIntegerNumbers, integerPartBuilder.length)
            integerPartBuilder.delete(maxIntegerNumbers, integerPartBuilder.length)
            newSelection = newTextBuilder.length
        }
        return Pair(newTextBuilder, newSelection)
    }

    private fun handleDecimalPart(finalDecimalPart: String, newTextBuilder: StringBuilder,
                                  selection: Int): Pair<StringBuilder, Int> {
        var newSelection = selection
        if (finalDecimalPart.isEmpty()) {
            return Pair(newTextBuilder, newSelection)
        }
        if (finalDecimalPart.length > maxDecimalNumbers) {
            newTextBuilder.delete(newTextBuilder.length - finalDecimalPart.length
                    + maxDecimalNumbers, newTextBuilder.length)
            if (newSelection > newTextBuilder.length) {
                newSelection = newTextBuilder.length
            }
        }
        return Pair(newTextBuilder, newSelection)
    }

    private fun isDecimalSeparator(c: Char): Boolean = c == decimalSeparator
}