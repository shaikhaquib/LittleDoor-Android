package com.devative.littledoor.util

import android.app.Activity
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException


/**
 * Created by AQUIB RASHID SHAIKH on 04-09-2023.
 */
class QuoteManager(
    private val activity: Activity,
    private val txtQuote: TextView,
    private val txtQuoteAuthor: TextView
) {

    init {
        fetchQuoteOfTheDay()
    }
    private fun fetchQuoteOfTheDay() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://zenquotes.io/api/random")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to fetch quote of the day: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body.string()
                val quote = parseQuoteOfTheDay(responseBody)
                activity.runOnUiThread{
                    txtQuoteAuthor.text = "-- ${quote?.author}"
                    txtQuote.text = "\"${quote?.text}\""
                }
                println("Quote of the day: ${quote?.text} - ${quote?.author}")
            }
        })
    }

   private fun parseQuoteOfTheDay(responseBody: String?): Quote? {
        try {
            val jsonArray = JSONArray(responseBody)
            val jsonObject = jsonArray.getJSONObject(0)
            val text = jsonObject.getString("q")
            val author = jsonObject.getString("a")
            return Quote(text, author)
        } catch (e: Exception) {
            println("Failed to parse quote of the day: ${e.message}")
        }
        return null
    }

    data class Quote(val text: String, val author: String)
}