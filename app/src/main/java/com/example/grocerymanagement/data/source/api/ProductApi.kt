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

interface ProductApi {

    @Multipart
    @POST("products/save_private_product.php")
    fun addProductToInvent(
        @Part("user_id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("barcode") barcode: RequestBody,
        @Part("description") description: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("note") note: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("products/save_product.php")
    fun addPublicProductToInvent(
        @Field("user_id") userId: Int,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: String,
        @Field("note") note: String?
    ): Call<ResponseBody>

    @Multipart
    @POST("products/edit_product.php")
    fun editProductToInvent(
        @Part("user_id") userId: RequestBody,
        @Part("product_id") productId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("barcode") barcode: RequestBody,
        @Part("description") description: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("note") note: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("products/edit_product_without_image.php")
    fun editProductToInventWithoutImage(
        @Field("user_id") userId: String,
        @Field("product_id") productId: Int,
        @Field("name") name: String,
        @Field("barcode") barcode: String,
        @Field("description") description: String,
        @Field("quantity") quantity: String,
        @Field("note") note: String?
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("products/edit_public_product.php")
    fun editPublicProductToInvent(
        @Field("user_id") userId: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: String,
        @Field("note") note: String?
    ): Call<ResponseBody>


    @GET("products/get_list_inventory.php")
    fun getListProductInvent(
        @Query("user_id") userId: String
    ): Call<ResponseBody>

    @GET("products/get_info_product.php")
    fun getInfoProduct(
        @Query("barcode") barcode: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("products/delete_item_inventory.php")
    fun deleteItemInventory(
        @Field("user_id") userId: String,
        @Field("product_id") productId: String
    ): Call<ResponseBody>


}