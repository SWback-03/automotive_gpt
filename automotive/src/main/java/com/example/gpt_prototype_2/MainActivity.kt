package com.example.myautomotiveapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.example.gpt_prototype_2.R
import com.example.myautomotiveapp.api.ChatGptApi
import com.example.myautomotiveapp.api.ChatGptRequest
import com.example.myautomotiveapp.api.ChatGptResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var speechLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts = TextToSpeech(this, this)

        speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.let {
                    val spokenText = it[0]
                    getChatGPTResponse(spokenText)
                }
            }
        }

        startVoiceRecognition()
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechLauncher.launch(intent)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.getDefault()
        }
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    private fun getChatGPTResponse(inputText: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ChatGptApi::class.java)
        val request = ChatGptRequest(prompt = inputText, max_tokens = 150, temperature = 0.7)

        service.getCompletion(request).enqueue(object : Callback<ChatGptResponse> {
            override fun onResponse(call: Call<ChatGptResponse>, response: Response<ChatGptResponse>) {
                if (response.isSuccessful) {
                    val outputText = response.body()?.choices?.get(0)?.text ?: "No response"
                    speakOut(outputText)
                } else {
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChatGptResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Failed to get response", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}