package com.example.myautomotiveapp.api

data class ChatGptRequest(
    val prompt: String,
    val max_tokens: Int,
    val temperature: Double
)

