package com.example.grocerymanagement.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.domain.model.ProductInfo
import com.example.grocerymanagement.domain.model.ShoppingListItem
import com.example.grocerymanagement.domain.serviceInterface.ShoppingRepository
import com.example.grocerymanagement.presentation.adapter.adapterItem.ShoppingItemProduct
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ShoppingRepositoryImpl(private val context: Context): ShoppingRepository {

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    private val _shoppingLists = MutableLiveData<List<ShoppingListItem>>()
    val shoppingLists: LiveData<List<ShoppingListItem>> get() = _shoppingLists

    private val _shoppingListItems = MutableLiveData<List<ShoppingItemProduct>>()
    val shoppingListItems: LiveData<List<ShoppingItemProduct>> get() = _shoppingListItems


    override fun addNewList(name: String) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""

        RetrofitClient.shoppingApi.addNewList(userId, name)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responseBody = response.body()?.string()
                    val errorBody = response.errorBody()?.string()

                    Log.d("AddList", "Server Response: $responseBody")
                    Log.d("AddList", "Error Response: $errorBody")

                    try {
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val message = jsonResponse.optString("message", "Có lỗi xảy ra")

                        if (jsonResponse.optBoolean("success", false)) {
                            _saveStatus.value = true
                        } else {
                            _saveStatus.value = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("AddList", "Lỗi xử lý JSON: ${e.message}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddList", "Request Failed: ${t.message}")
                    _saveStatus.value = false
                }
            })
    }

    override fun addProductToList(
        listId: String,
        name: String,
        barcode: String,
        description: String,
        quantity: String,
        note: String,
        imgFile: File
    ) {

        val listIdBody = RequestBody.create(MediaType.parse("text/plain"), listId)
        val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val barcodeBody = RequestBody.create(MediaType.parse("text/plain"), barcode)
        val descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description)
        val quantityBody = RequestBody.create(MediaType.parse("text/plain"), quantity) // Chuyển Int thành String
        val noteBody = RequestBody.create(MediaType.parse("text/plain"), note)

        val requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val imagePart = MultipartBody.Part.createFormData("img", imgFile.name, requestFile)

        RetrofitClient.shoppingApi.addProductToShoppingList(listIdBody, nameBody, barcodeBody, descriptionBody, quantityBody, noteBody,imagePart)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responseBody = response.body()?.string()
                    val errorBody = response.errorBody()?.string()

                    Log.d("AddProduct", "Server Response: $responseBody")
                    Log.d("AddProduct", "Error Response: $errorBody")

                    try {
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val message = jsonResponse.optString("message", "Có lỗi xảy ra")

                        if (jsonResponse.optBoolean("success", false)) {
                            _saveStatus.value = true
                        } else {
                            _saveStatus.value = false
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("AddProduct", "Lỗi xử lý JSON: ${e.message}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddProduct", "Request Failed: ${t.message}")
                    _saveStatus.value = false
                }
            })
    }

    override fun addProductToList(productId: Int, quantity: Int) {

        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: "" // Lấy user_id từ SharedPreferences

        RetrofitClient.shoppingApi.addProductToShoppingList(userId,productId,quantity)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responseBody = response.body()?.string()
                    val errorBody = response.errorBody()?.string()

                    Log.d("AddProduct", "Server Response: $responseBody")
                    Log.d("AddProduct", "Error Response: $errorBody")

                    try {
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val message = jsonResponse.optString("message", "Có lỗi xảy ra")

                        if (jsonResponse.optBoolean("success", false)) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("AddProduct", "Lỗi xử lý JSON: ${e.message}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("AddProduct", "Request Failed: ${t.message}")
                }
            })
    }

    override fun getShoppingLists() {

        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""
        Log.d("DEBUG_API", "userId: $userId")

        RetrofitClient.shoppingApi.getListShopping(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("DEBUG_API", "Đã vào onResponse")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)
                        if (jsonObject.getBoolean("success")) {
                            val listArray = jsonObject.getJSONArray("data")
                            val shoppingLists = mutableListOf<ShoppingListItem>()

                            for (i in 0 until listArray.length()) {
                                val item = listArray.getJSONObject(i)
                                val listItem = ShoppingListItem(
                                    id = item.getInt("id"),
                                    name = item.getString("name"),
                                    isFavorite = item.getInt("is_favorite"),
                                    itemCount = item.getInt("item_count"),
                                    createdAt = item.getString("created_at"),
                                    updatedAt = item.getString("updated_at")
                                )
                                shoppingLists.add(listItem)
                            }

                            // Ví dụ: cập nhật LiveData
                            _shoppingLists.postValue(shoppingLists)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching shopping lists: ${t.message}")
            }
        })
    }

    override fun deleteShoppingList(shoppingListId: Int) {
        RetrofitClient.shoppingApi.deleteShoppingList(shoppingListId).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("DELETE", "Xóa thành công")
                } else {
                    Log.e("DELETE", "Xóa thất bại")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("DELETE", "Lỗi: ${t.message}")
            }

        })
    }

    override fun deleteShoppingItem(itemId: Int, productId: Int) {
        RetrofitClient.shoppingApi.deleteShoppingListItem(itemId,productId).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("DELETE", "Xóa thành công")
                } else {
                    Log.e("DELETE", "Xóa thất bại")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("DELETE", "Lỗi: ${t.message}")
            }
        })
    }


    override fun addFav(shoppingListId: Int) {
        RetrofitClient.shoppingApi.addFav(shoppingListId).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("UPDATE", "Thêm thành công")
                } else {
                    Log.e("UPDATE", "Thêm thất bại")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UPDATE", "Lỗi: ${t.message}")
            }

        })
    }

    override fun addCheck(itemId: Int) {
        RetrofitClient.shoppingApi.addCheck(itemId).enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("UPDATE", "Thêm thành công")
                } else {
                    Log.e("UPDATE", "Thêm thất bại")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("UPDATE", "Lỗi: ${t.message}")
            }

        })
    }

    override fun getShoppingItem(shoppingListItem: Int) {
        RetrofitClient.shoppingApi.getShoppingItem(shoppingListItem).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)

                        if (jsonObject.getBoolean("success")) {
                            val itemsArray = jsonObject.getJSONArray("items")
                            val shoppingItems = mutableListOf<ShoppingItemProduct>()

                            for (i in 0 until itemsArray.length()) {
                                val item = itemsArray.getJSONObject(i)

                                val shoppingItem = ShoppingItemProduct(
                                    id = item.getInt("id"),
                                    shoppingListId = item.getInt("shoppingListId"),
                                    productId = item.getInt("productId"),
                                    quantity = item.getInt("quantity"),
                                    isChecked = item.getInt("isChecked"),
                                    productName = item.getString("productName"),
                                    barcode = item.optString("barcode", null.toString()),
                                    img = item.optString("img", null.toString())
                                )
                                shoppingItems.add(shoppingItem)
                            }

                            // Gán vào LiveData nếu bạn có dùng
                            _shoppingListItems.postValue(shoppingItems)
                        } else {
                            Log.e("SHOP_ITEM_API", "Không tìm thấy item: ${jsonObject.getString("message")}")
                        }
                    }
                } else {
                    Log.e("SHOP_ITEM_API", "Lỗi response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("SHOP_ITEM_API", "Lỗi kết nối: ${t.message}")
            }
        })
    }

    override fun getProductFromServer(barcode: String): LiveData<ProductInfo?> {
        val result = MutableLiveData<ProductInfo?>()

        RetrofitClient.productApi.getInfoProduct(barcode)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val jsonResponse = JSONObject(response.body()?.string() ?: "{}")
                        val productInfo = parseProductInfoFromJson(jsonResponse)
                        result.postValue(productInfo)  // Trả kết quả về
                    } else {
                        result.postValue(null) // Trả về null khi không tìm thấy sản phẩm
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    result.postValue(null) // Trả về null khi thất bại
                    Log.e("API_ERROR", "Error: ${t.message}")
                }
            })
        return result
    }

    override fun saveProductToList(shoppingListId: Int, productId: Int, quantity: Int) {
        RetrofitClient.shoppingApi.saveProductToList(shoppingListId, productId, quantity)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    val responseBody = response.body()?.string()
                    val errorBody = response.errorBody()?.string()

                    Log.d("SaveProduct", "Server Response: $responseBody")
                    Log.d("SaveProduct", "Error Response: $errorBody")

                    try {
                        val jsonResponse = JSONObject(responseBody ?: "{}")
                        val message = jsonResponse.optString("message", "Có lỗi xảy ra")

                        if (jsonResponse.optBoolean("success", false)) {
                            Toast.makeText(context, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                            _saveStatus.postValue(true)
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            _saveStatus.postValue(false)
                        }
                    } catch (e: Exception) {
                        Log.e("SaveProduct", "Lỗi xử lý JSON: ${e.message}")
                        _saveStatus.postValue(false)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("SaveProduct", "Request Failed: ${t.message}")
                    Toast.makeText(context, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    _saveStatus.postValue(false)
                }
            })
    }


    private fun parseProductInfoFromJson(jsonResponse: JSONObject): ProductInfo? {
        return try {
            if (jsonResponse.getBoolean("success")) {
                val item = jsonResponse.getJSONObject("product")
                ProductInfo(
                    id = item.getInt("id"),
                    name = item.getString("name"),
                    barcode = item.getString("barcode"),
                    img = item.getString("img"),
                    description = item.getString("description"),
                    isPrivate = item.getInt("is_private")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProductParseError", "Error parsing product data: ${e.message}")
            null
        }
    }


}