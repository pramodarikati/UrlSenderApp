package com.example.urlsenderapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var urlSender: UrlSender
    private lateinit var logTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logTextView = findViewById(R.id.logTextView)

        urlSender = UrlSender(this) { log ->
            runOnUiThread {
                logTextView.append("\n$log")
            }
        }

        urlSender.startSending()
    }
}

