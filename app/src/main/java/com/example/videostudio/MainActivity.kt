package com.example.videostudio

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        val title = TextView(this)
        title.text = "استوديو الفيديو يعمل بنجاح"
        title.textSize = 22f
        val button = Button(this)
        button.text = "ابدأ"
        button.setOnClickListener { title.text = "جاهز للخطوة التالية" }
        layout.addView(title)
        layout.addView(button)
        setContentView(layout)
    }
}
