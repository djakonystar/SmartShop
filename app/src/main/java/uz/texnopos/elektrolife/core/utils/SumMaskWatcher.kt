package uz.texnopos.elektrolife.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText

class SumMaskWatcher(private val editText: EditText) : TextWatcher {
    private lateinit var before: String
    private var cursorBefore = 0
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        before = p0.toString()
        cursorBefore = editText.selectionStart
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        editText.removeTextChangedListener(this)
        val text = editText.text.toString()
        val price = text.filter { it.isDigit() || it == '.' }
        var cursorPosition = editText.selectionStart

        val cursorAtTheEnd = cursorPosition == editText.length()
        editText.setText(price.sumFormat)
        val afterPoint = if (before.contains('.')) before.substringAfter('.') else ""
        val range = if (afterPoint.length == 2) before.length - 2..before.length else 0..0
        if (cursorAtTheEnd) cursorPosition = editText.length()
        else {
            if (cursorPosition > 0) {
                if (editText.length() < before.length) {
                    val space = before[cursorPosition - 1] == ' '
                    if (!space) {
                        val ss = before.substring(0, cursorBefore)
                        if (ss.length > 3 && ss.length % 3 == 1) {
                            Log.d("cursorPosition", "I'm in if! :)")
                            if (cursorBefore < before.length) {
                                cursorPosition = cursorBefore - 2
                                if (ss.filter { it != ' ' }.length % 3 != 1 || cursorBefore in range ||
                                    before.substringBefore(' ').length == 2
                                )
                                    if (!before.contains('.') && before.substringBefore(' ').length == 2 || cursorBefore in range) {
                                        cursorPosition++
                                    } else if (before.contains('.')) cursorPosition++
                                if ((!text.contains(' ') || before.substringBefore(' ').length == 3) && cursorBefore !in range) cursorPosition++
//                                if (before.substringBefore(' ').length == 1 && !before.contains('.')) cursorPosition--
                            }
                        } else {
                            Log.d("cursorPosition", "I'm in else! :)")
                            if (cursorBefore < before.length) {
                                cursorPosition = cursorBefore - 2
                                if (ss.filter { it != ' ' }.length % 3 != 1 || cursorBefore in range ||
                                    before.substringBefore(' ').length == 2
                                ) if (!before.contains('.') && before.substringBefore(' ').length == 2 || cursorBefore in range) cursorPosition++
                                if ((!text.contains(' ') || before.substringBefore(' ').length == 3) && cursorBefore !in range)
                                    cursorPosition++
                                if (before.substringBefore(' ').length == 1 && !before.contains('.')) {
//                                    cursorPosition--
                                }
                            }
                        }
                    } else {
                        Log.d("cursorPosition", "I'm in space! :)")
                        val ss = text.substringBefore(' ')
                        if (ss.filter { it != ' ' }.length % 3 == 1) cursorPosition--
                    }
                } else if (editText.length() > before.length) {
                    val l = before.length - cursorPosition
                    cursorPosition = editText.length() - l - 1
                } else {
                    Log.d("cursorPosition", "I'm in equals! :)")
                    cursorPosition = cursorBefore
                    if (before[cursorBefore - 1] == ' ') cursorPosition--
                }
            }
        }
        editText.setSelection(cursorPosition)
        editText.addTextChangedListener(this)
    }

    private val String.sumFormat: String
        get() {
            val beforePoint = this.substringBefore('.')
            var text = beforePoint.reversed()
            text = text.chunked(3).joinToString(" ")
            if (this.contains('.')) {
                val afterPoint = this.substringAfter('.')
                return "${text.reversed()}.$afterPoint"
            }
            return text.reversed()
        }
}
