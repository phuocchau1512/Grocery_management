package com.example.grocerymanagement.data.source.api

import com.example.grocerymanagement.domain.model.CreateUserReq
import com.example.grocerymanagement.domain.model.LoginRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthApi {
    @Headers("Content-Type: application/json")
    @POST("users/signup.php")
    fun signUp(@Body request: CreateUserReq): Call<ResponseBody>

    @Headers("Content-Type: application/json")
    @POST("users/signin.php") // Đường dẫn API PHP của bạn
    fun login(@Body request: LoginRequest): Call<ResponseBody>
}