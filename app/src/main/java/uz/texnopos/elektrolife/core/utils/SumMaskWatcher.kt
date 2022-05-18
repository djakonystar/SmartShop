package uz.texnopos.elektrolife.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.widget.EditText
import uz.texnopos.elektrolife.core.extensions.notContains

class SumMaskWatcher(private val editText: EditText) : TextWatcher {
    private lateinit var before: String
    private lateinit var after: String
    private var cursorBefore = 0
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        before = p0.toString()
        cursorBefore = editText.selectionStart
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        after = p0.toString().filter { it != ' ' }.sumFormat
        editText.removeTextChangedListener(this)
        var cursorPosition = editText.selectionStart

        editText.setText(after)
//        val afterPoint = if (before.contains('.')) before.substringAfter('.') else ""
//        val range = if (afterPoint.length == 2) before.length - 2..before.length else 0..0

        val cursorAtTheEnd = cursorBefore == before.length
        if (cursorAtTheEnd) cursorPosition = editText.length()
        else {
            if (cursorPosition > 0) {
                if (after.length == before.length) {
                    log("I'm in equals! :)")
                    cursorPosition = cursorBefore
                    if (cursorBefore != 0) if (before[cursorBefore - 1] == ' ') {
                        log("I'm in equals-if-if! :)")
                        cursorPosition--
                    }
                    if (before.notContains('.') && after.contains('.')) {
                        log("I'm in equals-if2! :)")
                        if (cursorBefore == 0) cursorPosition++
                    }
                } else if (after.length > before.length) {
                    log("I'm in more! :)")
                    val l = before.length - cursorPosition
                    cursorPosition = editText.length() - l - 1
                    if (before.notContains('.') && after.contains('.')) {
                        log("I'm in more-if! :)")
                        if (cursorBefore == 0) cursorPosition = 2
                        else {
                            val ss = before.substring(0 until cursorBefore)
                            log("Spaces before: ${ss.count { it == ' ' }}")
                            log("Spaces after: ${after.substringBefore('.').count { it == ' ' }}")
                            if (ss.count { it == ' ' } - after.substringBefore('.')
                                    .count { it == ' ' } == 1) {
                                log("I'm checking for counts! :)")
                                cursorPosition = cursorBefore
                            }
                        }
                    }
                } else {
                    log("I'm in less! :)")
                    if (before.contains('.')) {
                        log("I'm in dot! :)")
                        cursorPosition = cursorBefore - 1
                        if (after.notContains('.')) {
                            cursorPosition =
                                before.substringBefore('.').filter { it != ' ' }.sumFormat.length
                        }
                    } else if (after.contains('.')) {
                        val beforeN = before.filter { it != ' ' }
                        val afterN = after.filter { it != ' ' }
                        val ss = beforeN.substring(0 until afterN.indexOf('.'))
                        val ssL = ss.sumFormat.length
                        cursorPosition = ssL + 1
                        if (cursorBefore == 0) cursorPosition = 2
                    } else {
                        log("I'm in non-dot! :)")
                        cursorPosition = cursorBefore - 1
                        if (before.substringBefore(' ').length == 1) {
                            log("I'm in non-dot-if! :)")
                            cursorPosition--
                        }
                        if (after.contains('.')) {
                            log("I'm in non-dot-if2! :)")
                            cursorPosition++
                        }
                        if (cursorBefore > 0) {
                            log("I'm in non-dot-if3! :)")
                            if (before[cursorBefore - 1] == ' ') cursorPosition++
                        }
                    }
                }
//                if (editText.length() < before.length) {
//                    val space = before[cursorPosition - 1] == ' '
//                    if (!space) {
//                        val ss = before.substring(0, cursorBefore)
//                        if (ss.length > 3 && ss.length % 3 == 1) {
//                            Log.d("cursorPosition", "I'm in if! :)")
//                            if (cursorBefore < before.length) {
//                                cursorPosition = cursorBefore - 2
//                                if (ss.filter { it != ' ' }.length % 3 != 1 || cursorBefore in range ||
//                                    before.substringBefore(' ').length == 2
//                                )
//                                    if (!before.contains('.') && before.substringBefore(' ').length == 2 || cursorBefore in range) {
//                                        cursorPosition++
//                                    } else if (before.contains('.')) cursorPosition++
//                                if ((!text.contains(' ') || before.substringBefore(' ').length == 3) && cursorBefore !in range) cursorPosition++
////                                if (before.substringBefore(' ').length == 1 && !before.contains('.')) cursorPosition--
//                            }
//                        } else {
//                            Log.d("cursorPosition", "I'm in else! :)")
//                            if (cursorBefore < before.length) {
//                                cursorPosition = cursorBefore - 2
//                                if (ss.filter { it != ' ' }.length % 3 != 1 || cursorBefore in range ||
//                                    before.substringBefore(' ').length == 2
//                                ) if (!before.contains('.') && before.substringBefore(' ').length == 2 || cursorBefore in range) cursorPosition++
//                                if ((!text.contains(' ') || before.substringBefore(' ').length == 3) && cursorBefore !in range)
//                                    cursorPosition++
//                                if (before.substringBefore(' ').length == 1 && !before.contains('.')) {
////                                    cursorPosition--
//                                }
//                            }
//                        }
//                    } else {
//                        Log.d("cursorPosition", "I'm in space! :)")
//                        val ss = text.substringBefore(' ')
//                        if (ss.filter { it != ' ' }.length % 3 == 1) cursorPosition--
//                    }
//                } else if (editText.length() > before.length) {
//
//                } else {
//                    Log.d("cursorPosition", "I'm in equals! :)")
//
//                }
            }
        }
        editText.setSelection(cursorPosition)
        editText.addTextChangedListener(this)
    }

    private fun log(message: String) {
        Log.d("cursorPosition", message)
    }

    private val String.sumFormat: String
        get() {
            val beforePoint = this.substringBefore('.')
            var text = beforePoint.reversed()
            text = text.chunked(3).joinToString(" ")
            if (this.contains('.')) {
                val afterPoint = this.substringAfter('.')
                return "${text.reversed().ifEmpty { "0" }}.${
                    if (afterPoint.length > 1) afterPoint.substring(0..1) else afterPoint
                }"
            }
            return text.reversed()
        }
}
