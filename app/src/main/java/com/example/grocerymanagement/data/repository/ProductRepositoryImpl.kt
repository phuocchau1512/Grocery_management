package com.example.grocerymanagement.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.grocerymanagement.domain.model.Product
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.domain.model.ProductInfo
import com.example.grocerymanagement.domain.serviceInterface.ProductRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ProductRepositoryImpl(private val context: Context): ProductRepository {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    override fun addProductToInvent(name: String, barcode: String, description: String, quantity: String, note: String, imgFile: File) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: "" // Lấy user_id từ SharedPreferences

        val userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId)
        val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val barcodeBody = RequestBody.create(MediaType.parse("text/plain"), barcode)
        val descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description)
        val quantityBody = RequestBody.create(MediaType.parse("text/plain"), quantity) // Chuyển Int thành String
        val noteBody = RequestBody.create(MediaType.parse("text/plain"), note)

        val requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val imagePart = MultipartBody.Part.createFormData("img", imgFile.name, requestFile)

        RetrofitClient.productApi.addProductToInvent(userIdBody, nameBody, barcodeBody, descriptionBody, quantityBody, noteBody,imagePart)
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

    override fun addProductToInvent(productId: Int, quantity: String, note: String) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: "" // Lấy user_id từ SharedPreferences

        val call = userId.toIntOrNull()?.let {
            RetrofitClient.productApi.addPublicProductToInvent(
                it, productId, quantity, note)
        }


        call?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("EditProduct", "Server Response: $responseBody")
                Log.d("EditProduct", "Error Response: $errorBody")

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

    override fun editProductToInvent(
        productId: Int,
        name: String,
        barcode: String,
        description: String,
        quantity: String,
        note: String,
        imgFile: File?
    ) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: "" // Lấy user_id từ SharedPreferences

        val userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId)

        val productIdBody = RequestBody.create(MediaType.parse("text/plain"), productId.toString())
        val nameBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val barcodeBody = RequestBody.create(MediaType.parse("text/plain"), barcode)
        val descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description)
        val quantityBody = RequestBody.create(MediaType.parse("text/plain"), quantity)
        val noteBody = RequestBody.create(MediaType.parse("text/plain"), note)

        // Kiểm tra có ảnh không
        val imagePart = imgFile?.let {
            val requestFile = RequestBody.create(MediaType.parse("image/*"), it)
            MultipartBody.Part.createFormData("img", it.name, requestFile)
        }

        val call = if (imagePart != null) {
            RetrofitClient.productApi.editProductToInvent(
                userIdBody, productIdBody, nameBody, barcodeBody, descriptionBody, quantityBody, noteBody, imagePart
            )
        } else {
            RetrofitClient.productApi.editProductToInventWithoutImage(
                userId, productId, name, barcode, description, quantity, note
            )
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("EditProduct", "Server Response: $responseBody")
                Log.d("EditProduct", "Error Response: $errorBody")

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

    override fun editPublicProductToInvent(productId: Int, quantity: String, note: String) {

        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: "" // Lấy user_id từ SharedPreferences

        val call = RetrofitClient.productApi.editPublicProductToInvent(
            userId, productId, quantity, note)


        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("EditProduct", "Server Response: $responseBody")
                Log.d("EditProduct", "Error Response: $errorBody")

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

    // Phân tích dữ liệu JSON trả về từ server thành đối tượng Product
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


    override fun getProducts() {

        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""

        RetrofitClient.productApi.getListProductInvent(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)
                        if (jsonObject.getBoolean("success")) {
                            val productArray = jsonObject.getJSONArray("products")
                            val productList = mutableListOf<Product>()
                            for (i in 0 until productArray.length()) {
                                val item = productArray.getJSONObject(i)
                                val product = Product(
                                    id = item.getInt("id"),
                                    name = item.getString("name"),
                                    barcode = item.getString("barcode"),
                                    img = item.getString("img"),
                                    description = item.getString("description"),
                                    quantity = item.getInt("quantity"),
                                    note = item.getString("note"),
                                    is_private = item.getInt("is_private")
                                )
                                productList.add(product)
                            }
                            _products.postValue(productList) // Cập nhật danh sách sản phẩm
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching products: ${t.message}")
            }
        })
    }

    override fun deleteProductFromInventory(productId: String){
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""

        RetrofitClient.productApi.deleteItemInventory(userId,productId).enqueue(object : Callback<ResponseBody>{
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





}