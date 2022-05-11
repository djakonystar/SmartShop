package uz.texnopos.elektrolife.core.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import uz.texnopos.elektrolife.core.extensions.sumFormat

class SumMaskWatcher(private val editText: EditText) : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        editText.removeTextChangedListener(this)
        val price = editText.text.toString().filter { it.isDigit() || it == '.' }
        editText.setText(price.sumFormat)
        editText.setSelection(editText.length())
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
