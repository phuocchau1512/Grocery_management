package com.example.grocerymanagement.domain.chatbot

import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.presentation.adapter.chatbot.ChatResponse
import com.example.grocerymanagement.presentation.adapter.chatbot.PromptRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object PromptSender {

    interface PromptCallback {
        fun onSuccess(responseText: String)
        fun onError(errorMessage: String)
    }

    fun send(prompt: String, callback: PromptCallback) {
        val promptRequest = PromptRequest(prompt)

        RetrofitClient.chatBotApi.sendPrompt(promptRequest)
            .enqueue(object : Callback<ChatResponse> {

                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    if (response.isSuccessful) {
                        val chatResponse = response.body()
                        if (chatResponse != null) {
                            if (chatResponse.responseStatus == 0) {
                                callback.onSuccess(chatResponse.responseText ?: "Không có phản hồi.")
                            } else {
                                callback.onError("Lỗi từ máy chủ: ${chatResponse.responseText ?: "Không rõ lỗi."} (Status: ${chatResponse.responseStatus})")
                            }
                        } else {
                            callback.onError("Phản hồi rỗng từ máy chủ.")
                        }
                    } else {
                        val errorBodyString = try {
                            response.errorBody()?.string() ?: "Không có thông tin lỗi."
                        } catch (e: Exception) {
                            "Không thể đọc nội dung lỗi: ${e.message}"
                        }
                        callback.onError("Lỗi kết nối: ${response.code()} - ${response.message()}. Chi tiết: $errorBodyString")
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    callback.onError("Lỗi mạng hoặc hệ thống: ${t.localizedMessage}")
                }
            })
    }
}
