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
        Log.d("cursorPosition", "Before: ${p0}")
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        editText.removeTextChangedListener(this)
        val text = editText.text.toString()
        val price = text.filter { it.isDigit() || it == '.' }
        var cursorPosition = editText.selectionStart

//        cursorPosition += editText.text.toString().substring(0, cursorPosition).count { it == ' ' }
        Log.d("cursorPosition", "Cursor: $cursorPosition")
        Log.d("cursorPosition", "CursorBefore: $cursorBefore")
        val cursorAtTheEnd = cursorPosition == editText.length()
        editText.setText(price.sumFormat)
        Log.d("cursorPosition", "After: ${editText.text}")
        if (cursorAtTheEnd) cursorPosition = editText.length()
        else {
            if (cursorPosition > 0) {
                if (editText.length() < before.length) {
                    val space = before[cursorPosition - 1] == ' '
                    Log.d("cursorPosition", "Was Space: $space")
                    if (!space) {
                        val ss = before.substring(0, cursorBefore)
                        cursorPosition = if (ss.length > 3 && ss.length % 3 == 1) cursorBefore - 2
                        else cursorBefore - 1
                    } else {
                        val ss = text.substringBefore(' ')
                        if (ss.filter { it != ' ' }.length % 3 == 1) cursorPosition--
                    }
                } else if (editText.length() > before.length) {
                    val l = before.length - cursorPosition
                    cursorPosition = editText.length() - l - 1
                }
            }
        }
        Log.d("cursorPosition", "CursorFinal: $cursorPosition")
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
