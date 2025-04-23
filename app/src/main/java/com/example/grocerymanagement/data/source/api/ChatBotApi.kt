package com.example.grocerymanagement.data.source.api

import com.example.grocerymanagement.presentation.adapter.chatbot.ChatResponse
import com.example.grocerymanagement.presentation.adapter.chatbot.PromptRequest
import com.google.android.gms.common.api.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatBotApi {
    @POST("gemini/requestHandler.php") // Đường dẫn tương đối đến file PHP của bạn
    fun sendPrompt(@Body request: PromptRequest): Call<ChatResponse> // ChatResponse là data class cho response JSON
}