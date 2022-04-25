package uz.texnopos.elektrolife.core.utils

import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import java.io.*
import java.util.logging.Logger

class MyPrintDocumentAdapter(private val context: Context, private val pathName: String) :
    PrintDocumentAdapter() {

    companion object {
        private val LOG = Logger.getLogger(this::class.java.name)
    }

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val builder = PrintDocumentInfo.Builder("my_file.pdf")
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
            .build()

        callback.onLayoutFinished(builder, oldAttributes != newAttributes)
    }

    override fun onWrite(
        pageRange: Array<out PageRange>?,
        parcelFileDescriptor: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        writeResultCallback: WriteResultCallback
    ) {
        var input: InputStream? = null
        var output: OutputStream? = null
        try {
            val file = File(pathName)
            input = FileInputStream(file)
            output = FileOutputStream(parcelFileDescriptor.fileDescriptor)

            val buf = ByteArray(16384)
            var size: Int

            while (input.read(buf).also { size = it } >= 0
                && !cancellationSignal!!.isCanceled) {
                output.write(buf, 0, size)
            }

            if (cancellationSignal!!.isCanceled) {
                writeResultCallback.onWriteCancelled()
            } else {
                writeResultCallback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
            }
        } catch (e: Exception) {
            writeResultCallback.onWriteFailed(e.message)
            LOG.warning(e.message)
        } finally {
            try {
                input!!.close()
                output!!.close()
            } catch (e: IOException) {
                LOG.warning(e.message)
            }
        }
    }
}