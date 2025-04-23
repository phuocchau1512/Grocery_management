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

interface ShoppingApi {

    @FormUrlEncoded
    @POST("shopping/add_new_list.php")
    fun addNewList(
        @Field("user_id") userId: String,
        @Field("name") note: String
    ): Call<ResponseBody>

    @Multipart
    @POST("shopping/save_private_product_to_list.php")
    fun addProductToShoppingList(
        @Part("shopping_list_id") shoppingListId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("barcode") barcode: RequestBody,
        @Part("description") description: RequestBody,
        @Part("quantity") quantity: RequestBody,
        @Part("note") note: RequestBody,
        @Part img: MultipartBody.Part
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/add_shopping_to_inventory.php")
    fun addProductToShoppingList(
        @Field("user_id") userId: String,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/get_shopping_list.php")
    fun getListShopping(
        @Field("user_id") userId: String
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/delete_shopping_list.php")
    fun deleteShoppingList(
        @Field("shopping_list_id") shoppingListId: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/delete_shopping_item.php")
    fun deleteShoppingListItem(
        @Field("id") id: Int,
        @Field("product_id") productId:Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/add_fav_list.php")
    fun addFav(
        @Field("shopping_list_id") shoppingListId: Int,
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/add_check.php")
    fun addCheck(
        @Field("id") itemId: Int,
    ): Call<ResponseBody>

    @GET("shopping/get_shopping_item.php")
    fun getShoppingItem(
        @Query("shopping_list_id") shoppingListId: Int
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("shopping/save_product_to_list.php")
    fun saveProductToList(
        @Field("shopping_list_id") shoppingListId: Int,
        @Field("product_id") productId: Int,
        @Field("quantity") quantity: Int,
    ): Call<ResponseBody>



}