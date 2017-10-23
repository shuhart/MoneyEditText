package com.shuhart.moneyedittext.controller

import android.util.Log

class DeleteController {

    private val tag = DeleteController::class.java.simpleName

    fun handDelete(s: CharSequence, beforeText: String, beforeSelection: Int,
                   currentText: String, currentSelection: Int): Pair<String, Int> {

        Log.d(tag, "currentText = $currentText, beforeText = $beforeText")

        if (!currentText.contains(".") && currentText.length > 10) {
            return Pair(beforeText, beforeSelection)
        }

        if (currentText.isEmpty()) {
            return Pair("", 0)
        }
        if (currentText.startsWith(".")) {
            return Pair("0" + currentText, currentSelection)
        }

        return Pair(currentText, currentSelection)
    }
}