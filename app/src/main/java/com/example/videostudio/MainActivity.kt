package com.example.videostudio

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.util.concurrent.Executors

class MainActivity : Activity() {
    private lateinit var status: TextView
    private lateinit var trimButton: Button
    private lateinit var saveButton: Button
    private var pickedUri: Uri? = null
    private var outputFile: File? = null
    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        status = TextView(this).apply {
            text = "الخطوة 1: اختر فيديو من المعرض"
            textSize = 18f
            gravity = Gravity.CENTER
        }
        val pickButton = Button(this).apply { text = "اختيار فيديو" }
        trimButton = Button(this).apply { text = "قص أول 5 ثواني"; isEnabled = false }
        saveButton = Button(this).apply { text = "حفظ النتيجة"; isEnabled = false }

        pickButton.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_GET_CONTENT).apply { type = "video/*" }, 1)
        }
        trimButton.setOnClickListener { startTrim() }
        saveButton.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/mp4"
                putExtra(Intent.EXTRA_TITLE, "trimmed_video.mp4")
            }, 2)
        }

        layout.addView(status)
        layout.addView(pickButton)
        layout.addView(trimButton)
        layout.addView(saveButton)
        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            pickedUri = data?.data
            status.text = "تم اختيار الفيديو ✓\nالخطوة 2: اضغط قص أول 5 ثواني"
            trimButton.isEnabled = true
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            val destUri = data?.data ?: return
            val file = outputFile ?: return
            executor.execute {
                try {
                    contentResolver.openOutputStream(destUri)?.use { out ->
                        file.inputStream().use { inp -> inp.copyTo(out) }
                    }
                    runOnUiThread { status.text = "تم حفظ الفيديو بنجاح ✓✓" }
                } catch (e: Exception) {
                    runOnUiThread { status.text = "خطأ بالحفظ: ${e.message}" }
                }
            }
        }
    }

    private fun startTrim() {
        val uri = pickedUri ?: return
        status.text = "جاري القص... انتظر قليلاً"
        trimButton.isEnabled = false
        executor.execute {
            try {
                val input = File(cacheDir, "input.mp4")
                contentResolver.openInputStream(uri)?.use { inp ->
                    input.outputStream().use { out -> inp.copyTo(out) }
                }
                val out = File(cacheDir, "trimmed.mp4")
                val cmd = "-y -i ${input.absolutePath} -t 5 -c copy ${out.absolutePath}"
                val session = FFmpegKit.execute(cmd)
                runOnUiThread {
                    if (ReturnCode.isSuccess(session.returnCode)) {
                        outputFile = out
                        status.text = "تم القص بنجاح ✓\nالخطوة 3: اضغط حفظ النتيجة"
                        saveButton.isEnabled = true
                    } else {
                        status.text = "فشل القص: ${session.failStackTrace}"
                        trimButton.isEnabled = true
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    status.text = "خطأ: ${e.message}"
                    trimButton.isEnabled = true
                }
            }
        }
    }
}
