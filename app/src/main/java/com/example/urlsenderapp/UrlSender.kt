package com.example.urlsenderapp

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class UrlSender(
    private val context: Context,
    private val logger: (String) -> Unit = { }
) {

    private val port = 11007
    private val host = "127.0.0.1"

    fun startSending() {
        CoroutineScope(Dispatchers.IO).launch {
            val urls = loadUrls()
            var index = 0

            logger("Loaded ${urls.size} URLs from assets")

            while (true) {
                if (urls.isNotEmpty()) {
                    val url = urls[index % urls.size]
                    sendMessage(url)
                    index++
                }
                delay(30_000) // 30 seconds
            }
        }
    }

    private fun loadUrls(): List<String> {
        return try {
            context.assets.open("urls.txt")
                .bufferedReader().readLines().filter { it.isNotBlank() }
        } catch (e: Exception) {
            logger("Failed to read urls.txt: ${e.message}")
            emptyList()
        }
    }

    private fun sendMessage(message: String) {
        try {
            Socket(host, port).use { socket ->
                val out = PrintWriter(socket.getOutputStream(), true)
                out.println(message)
                logger("Sent URL: $message")
            }
        } catch (e: IOException) {
            if (e.message?.contains("Connection refused") == true) {
                logger("Receiver app not reachable. Will retry in 30 seconds.")
            } else {
                logger("Error sending message: ${e.message}")
            }
        }
    }

}
