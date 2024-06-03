package com.example.myautomotiveapp.api

data class ChatGptResponse(
    val choices: List<Choice>
)

data class Choice(
    val text: String
)

