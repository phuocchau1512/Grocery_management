package com.example.grocerymanagement.data.source.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface RecipesApi {


    @Multipart
    @POST("recipes/add_recipes.php")
    fun addRecipes(
        @Part("user_id") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("time_minutes") time: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("recipes/add_like.php")
    fun addLike(
        @Field("user_id") itemId: String,
        @Field("recipe_id") recipeId: Int
    ): Call<ResponseBody>

    @Multipart
    @POST("recipes/edit_recipe_with_image.php")
    fun editRecipes(
        @Part("id") itemId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("ingredients") ingredients: RequestBody,
        @Part("instructions") instructions: RequestBody,
        @Part("time_minutes") time: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("recipes/get_recipes.php")
    fun getRecipes(
        @Query("user_id") userId: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<ResponseBody>

    @GET("recipes/search_recipe.php")
    fun searchRecipes(
        @Query("user_id") userId: String,
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Call<ResponseBody>

    @GET("recipes/get_user_recipes.php")
    fun getUserRecipes(
        @Query("user_id") userId: String,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("recipes/edit_recipe_no_image.php")
    fun editRecipes(
        @Field("id") itemId: Int,
        @Field("title") title: String,
        @Field("ingredients") ingredients: String,
        @Field("instructions") instructions: String,
        @Field("time_minutes") time: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("recipes/delete_recipe.php")
    fun deleteRecipe(
        @Field("id") itemId: Int,
    ): Call<ResponseBody>




}