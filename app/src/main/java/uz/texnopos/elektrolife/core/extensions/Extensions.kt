package uz.texnopos.elektrolife.core.extensions

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.print.PrintAttributes
import android.print.PrintManager
import android.text.InputFilter
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.android.material.snackbar.Snackbar
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.utils.MyPrintDocumentAdapter
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.dialog.ErrorDialog
import uz.texnopos.elektrolife.ui.dialog.SuccessDialog
import uz.texnopos.elektrolife.ui.dialog.WarningDialog

fun Fragment.showMessage(msg: String?) {
    Toast.makeText(this.requireContext(), msg, Toast.LENGTH_LONG).show()
}

fun Fragment.showSnackbar(message: String) {
    Snackbar.make(this.requireView(), message, Snackbar.LENGTH_LONG).show()
}

fun Fragment.showError(message: String?): ErrorDialog {
    val errorDialog = ErrorDialog(message!!)
    errorDialog.show(this.requireActivity().supportFragmentManager, errorDialog.tag)
    return errorDialog
}

fun Fragment.showSuccess(message: String?): SuccessDialog {
    val successDialog = SuccessDialog(message!!)
    successDialog.show(this.requireActivity().supportFragmentManager, successDialog.tag)
    return successDialog
}

fun Fragment.showWarning(message: String?): WarningDialog {
    val warningDialog = WarningDialog(message!!)
    warningDialog.show(this.requireActivity().supportFragmentManager, warningDialog.tag)
    return warningDialog
}

fun ViewGroup.inflate(@LayoutRes id: Int): View =
    LayoutInflater.from(context).inflate(id, this, false)

fun RecyclerView.addVertDivider(context: Context?) {
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
}

fun RecyclerView.addHorizDivider(context: Context?) {
    this.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
}

fun View.onClick(onClick: (View) -> Unit) {
    this.setOnClickListener(onClick)
}

// Setting Html string to TextView and making links clickable
fun TextView.setTextViewHtml(html: String, onLinkClick: (link: String) -> Unit) {
    val sequence: CharSequence = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    val strBuilder = SpannableStringBuilder(sequence)
    val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
    for (span in urls) {
        makeLinkClickable(strBuilder, span, onLinkClick)
    }
    this.text = strBuilder
    this.movementMethod = LinkMovementMethod.getInstance()
}

private fun makeLinkClickable(
    strBuilder: SpannableStringBuilder,
    span: URLSpan,
    onLinkClick: (link: String) -> Unit
) {
    val start = strBuilder.getSpanStart(span)
    val end = strBuilder.getSpanEnd(span)
    val flags = strBuilder.getSpanFlags(span)
    val clickable = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val fullText = (widget as TextView).text.toString()
            val link = fullText.substring(start, end)
            onLinkClick.invoke(link)
        }
    }
    strBuilder.setSpan(clickable, start, end, flags)
    strBuilder.removeSpan(span)
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Int.dpToFloat: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

fun String.dialPhone(activity: Activity) {
    var phone = "+998"
    if (this.length == 9) phone += this
    else phone = this
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone)))
    activity.startActivity(intent)
}

val String.toSumFormat: String
    get() {
        var text = this.reversed()
        text = text.subSequence(0, text.length)
            .chunked(3) // group every 3 chars
            .joinToString(" ")
        return text.reversed()
    }

val String.sumFormat: String
    get() {
        val beforePoint = this.substringBefore('.')
        var text = beforePoint.reversed()
        text = text.chunked(3).joinToString(" ")
        if (this.contains('.')) {
            val afterPoint = this.substringAfter('.')
            if (afterPoint.length == 1) return "${text.reversed()}.${afterPoint}0"
            return "${text.reversed()}.$afterPoint"
        }
        return text.reversed()
    }

val Int.toSumFormat: String
    get() {
        var text = this.toString().reversed()
        text = text.subSequence(0, text.length)
            .chunked(3) // group every 3 chars
            .joinToString(" ")
        return text.reversed()
    }

val Number.toSumFormat: String
    get() {
        var text = this.toString().reversed()
        text = text.subSequence(0, text.length)
            .chunked(3) // group every 3 chars
            .joinToString(" ")
        return text.reversed()
    }

val Double.toSumFormat: String
    get() {
        var num = this.toLong().toSumFormat
        val formattedNum = "%.${2}f".format(this)
        val afterPoint = formattedNum.substring(formattedNum.length - 2, formattedNum.length)
        num += if (afterPoint == "0") ".00" else {
            if (afterPoint.length == 1) ".${afterPoint}0"
            else ".$afterPoint"
        }
        return num
    }

val String.toPhoneFormat: String
    get() {
        if (this.length == 13) {
            return this.substring(4).toPhoneFormat
        }
        return this.toPhoneNumber
    }

val String.toPhoneNumber: String
    get() {
        val arr = this.toCharArray()
        var phone = if (arr.size == 9) "(" else "+998 ("
        arr.forEachIndexed { index, c ->
            phone += c
            if (index == 1) {
                phone += ") "
            }
            if (index == 4 || index == 6) {
                phone += " "
            }
        }
        return phone
    }

/**
 * Change date format from _yyyy-MM-dd_ to _dd.MM.yyyy_ and vice versa
 */
val String.changeDateFormat: String
    get() {
        val day: String
        val month: String
        val year: String
        return if (this.contains('-')) {
            day = this.substring(8..9)
            month = this.substring(5..6)
            year = this.substring(0..3)
            "$day.$month.$year"
        } else {
            day = this.substring(0..1)
            month = this.substring(3..4)
            year = this.substring(6..9)
            "$year-$month-$day"
        }
    }

fun String.getOnlyDigits(): String {
    val s = this.filter { it.isDigit() }
    return s.ifEmpty { "0" }
}

fun CharSequence.notContains(char: Char, ignoreCase: Boolean = false): Boolean {
    return !this.contains(char, ignoreCase)
}

fun CharSequence.notContains(other: CharSequence, ignoreCase: Boolean = false): Boolean {
    return !this.contains(other, ignoreCase)
}

fun CharSequence.notContains(regex: Regex): Boolean {
    return !this.contains(regex)
}

@SuppressLint("SetTextI18n")
fun animateDebtPrice(start: Double, end: Double, textView: TextView, settings: Settings) {
    val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
    animator.addUpdateListener {
        val newValue = (it.animatedValue as Float).toDouble().format(2).toDouble
        textView.text = textView.context.getString(
            R.string.total_debt_text,
            newValue.checkModule.toSumFormat,
            settings.currency
        )
    }
    animator.duration = 500
    animator.start()
}

@SuppressLint("SetTextI18n")
fun animateTotalPrice(start: Double, end: Double, textView: TextView, settings: Settings) {
    val animator = ValueAnimator.ofFloat(start.toFloat(), end.toFloat())
    animator.addUpdateListener {
        val newValue = (it.animatedValue as Float).toDouble().format(2).toDouble
        textView.text = textView.context.getString(
            R.string.total_sum_text,
            newValue.checkModule.toSumFormat,
            settings.currency
        )
    }
    animator.duration = 500
    animator.start()
}

val EditText.filterForDouble: Unit
    @SuppressLint("SetTextI18n")
    get() {
        val filter = InputFilter { source, _, _, spanned, _, _ ->
            val afterPoint = if (spanned.contains('.')) {
                spanned.toString().substringAfter('.').length
            } else {
                0
            }

            val range = this.length() - 2..this.length()

            if (source != null && source.equals(".") && spanned.contains(".")) ""
            else if (source != null && afterPoint == 2 && this.selectionEnd in range) ""
            else if (source != null && source.equals(".") && spanned.isEmpty()) "0."
            else if (source != null && source.equals(".") && spanned.isNotEmpty()) "."
            else if (source != null && "-,.".contains("" + source)) ""
            else null
        }
        this.filters = arrayOf(filter)
    }

fun EditText.setBlockFilter(block: String) {
    val filter = InputFilter { source, _, _, _, _, _ ->
        if (source != null && block.contains("" + source)) "" else null
    }
    this.filters = arrayOf(filter)
}

val Double.checkModule: Number
    get() {
        return if (this % 1 == 0.0) this.toLong()
        else this
    }

fun Int.unitConverter(context: Context): String {
    return Constants.getUnitName(context, this)
}

infix fun Double.format(afterPoint: Int): String {
    val formatted = "%.${afterPoint}f".format(this)

    return if (formatted.contains(',')) {
        formatted.replace(',', '.')
    } else {
        formatted
    }
}

val String.toDouble: Double
    get() {
        if (this.isEmpty()) {
            return 0.0
        }
        return this.filter { it.isDigit() || it == '.' }.ifEmpty { "0.0" }.toDouble()
    }

fun Fragment.doPrint(filePath: String, fileName: String) {
    try {
        val printManager = requireActivity().getSystemService(Context.PRINT_SERVICE) as PrintManager
        val jobName = "${getString(R.string.app_name)} Document"
        printManager.print(
            jobName,
            MyPrintDocumentAdapter(requireContext(), filePath, fileName),
            PrintAttributes.Builder().build()
        )
    } catch (e: Exception) {
        showMessage(e.message)
    }
}

fun Fragment.pdfGenerator(
    view: View,
    fileName: String,
    onSuccess: (response: SuccessResponse?) -> Unit,
    onFailure: (failureResponse: FailureResponse?) -> Unit
) {
    this.context?.let { context ->
        PdfGenerator.Builder()
            .setContext(context)
            .fromViewSource()
            .fromView(view)
            .setFileName(fileName)
            .setFolderNameOrPath(this.requireActivity().packageName)
            .openPDAfterGeneration(false)
            .build(object : PdfGeneratorListener() {
                override fun onStartPDFGeneration() {

                }

                override fun onFinishPDFGeneration() {

                }

                override fun onSuccess(response: SuccessResponse?) {
                    super.onSuccess(response)
                    onSuccess.invoke(response)
                }

                override fun onFailure(failureResponse: FailureResponse?) {
                    super.onFailure(failureResponse)
                    onFailure.invoke(failureResponse)
                }

                override fun showLog(log: String?) {
                    super.showLog(log)
                    log?.let { Log.d("XMLtoPDF", it) }
                }
            })
    }
}

val Fragment.checkForPermissions: Unit
    get() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestMultiplePermissions().launch(
                arrayOf(
                    Manifest.permission.CAMERA
                )
            )
        } else {
//            showMessage("Permission already granted!")
        }
    }

private fun Fragment.requestMultiplePermissions() =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isGranted = true
        permissions.entries.forEach {
            if (!it.value) isGranted =
                false
        }
        if (!isGranted) {
            val dialog = showWarning("Permission required")
            dialog.setOnPositiveButtonClickListener {
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                requireActivity().startActivity(intent)
            }
            dialog.setOnDismissListener {
                findNavController().popBackStack()
            }
            if (dialog.isAdded) {
                requireActivity().supportFragmentManager.beginTransaction().show(dialog)
            }
        } else {
//            showMessage("Permission granted!")
        }
    }

fun <T: Any> T.scope(action: (T) -> Unit) {
    action(this)
}

fun String.isNotEmptyAndBlank() = this.isNotEmpty() && this.isNotBlank()

fun String.isEmptyOrBlank() = this.isEmpty() || this.isBlank()
