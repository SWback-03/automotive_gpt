package com.example.myautomotiveapp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGptApi {
    @Headers("Content-Type: application/json", "Authorization: Bearer YOUR_API_KEY")
    @POST("v1/engines/davinci-codex/completions")
    fun getCompletion(@Body request: ChatGptRequest): Call<ChatGptResponse>
}

