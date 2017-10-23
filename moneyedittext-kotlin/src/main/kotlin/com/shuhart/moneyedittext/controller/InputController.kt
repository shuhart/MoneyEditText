package com.shuhart.moneyedittext.controller

class InputController(private val decimalSeparator: Char,
                      private val maxDecimalNumbers: Int = 2,
                      private val maxIntegerNumbers: Int = 10) {

    private val tag = InputController::class.java.simpleName

    fun handleInput(realText: String, realSelection: Int, beforeText: String, beforeSelection: Int, s: CharSequence, start: Int, count: Int): Pair<String, Int> {
        val input = s.substring(start, start + count)
        val (correctText, correctSelection) = correctText(input, beforeText, beforeSelection,
                realText, realSelection)
        return Pair(correctText, correctSelection)
    }

    private fun correctText(input: String, beforeText: String, beforeSelection: Int,
                            currentText: String, currentSelection: Int): Pair<String, Int> {

        if (isDecimalSeparator(input)) {
            if (beforeText.contains(decimalSeparator)) {
                return Pair(beforeText, beforeSelection)
            }
            val numbersBeforeSeparator = (0 until currentText.length)
                    .takeWhile { !isDecimalSeparator(currentText.substring(it, it + 1)) }
                    .count()
            if (numbersBeforeSeparator == 0) {
                return Pair(beforeText, beforeSelection)
            }
            val reversed = currentText.reversed()
            val numbersAfterSeparator = (0 until reversed.length)
                    .takeWhile { !isDecimalSeparator(reversed.substring(it, it + 1)) }
                    .count()
            if (numbersAfterSeparator > maxDecimalNumbers) {
                return Pair(beforeText, beforeSelection)
            }
            return Pair(currentText, currentSelection)
        }

        if (!isNumber(input)) {
            return Pair(beforeText, beforeSelection)
        }

        val decimalSeparatorExist = currentText.contains(decimalSeparator)
        if (!decimalSeparatorExist) {
            var integerNumberCount = 0
            for (i in 0 until currentText.length) {
                if (isNumber(currentText.substring(i, i + 1))) integerNumberCount++
            }
            if (integerNumberCount > maxIntegerNumbers) {
                return Pair(beforeText, beforeSelection)
            }

            if (input == "0") {
                if (beforeText == "0") {
                    return Pair(beforeText, beforeSelection)
                }
                if (beforeText.isNotEmpty() && currentText.startsWith("0")) {
                    return Pair(beforeText, beforeSelection)
                }
                return Pair(currentText, currentSelection)
            }
            if (beforeText == "0") {
                return Pair(input, 1)
            }
            return Pair(currentText, currentSelection)
        }
        val decimalSeparatorIndex = currentText.indexOf(decimalSeparator)
        if (currentSelection <= decimalSeparatorIndex) {
            var integerNumberCount = 0
            for (i in 0 until currentText.length) {
                val symbol = currentText.substring(i, i + 1)
                if (isDecimalSeparator(symbol)) break
                if (isNumber(symbol)) integerNumberCount++
            }
            if (integerNumberCount > maxIntegerNumbers) {
                return Pair(beforeText, beforeSelection)
            }
            if (input == "0") {
                if (currentText.startsWith("0")) {
                    return Pair(beforeText, beforeSelection)
                }
                return Pair(currentText, currentSelection)
            }
            if (beforeText.startsWith("0")) {
                val newSelection = if (beforeSelection == 0 || beforeSelection == 1) 1 else beforeSelection
                return Pair(beforeText.replaceFirst("0", input), newSelection)
            }

            return Pair(currentText, currentSelection)
        }
        var decimalNumberCount = 0
        val reversed = currentText.reversed()
        for (i in 0 until reversed.length) {
            val symbol = reversed.substring(i, i + 1)
            if (isDecimalSeparator(symbol)) break
            decimalNumberCount++
        }
        if (decimalNumberCount > maxDecimalNumbers) {
            return Pair(beforeText, beforeSelection)
        }
        return Pair(currentText, currentSelection)
    }

    private fun isDecimalSeparator(cs: CharSequence): Boolean = decimalSeparator.toString() == cs

    private fun isNumber(cs: CharSequence): Boolean = "1234567890 ".contains(cs, false)
}