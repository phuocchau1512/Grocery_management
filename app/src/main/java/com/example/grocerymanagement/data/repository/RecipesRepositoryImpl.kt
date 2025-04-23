package com.example.grocerymanagement.data.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.domain.serviceInterface.RecipesRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RecipesRepositoryImpl(private val context: Context) : RecipesRepository {


    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> get() = _saveStatus

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _userRecipes = MutableLiveData<List<Recipe>>()
    val userRecipes: LiveData<List<Recipe>> get() = _userRecipes

    private val _searchRecipes = MutableLiveData<List<Recipe>>()
    val searchRecipes: LiveData<List<Recipe>> get() = _searchRecipes


    override fun addRecipes(
        title: String,
        ingredients: String,
        instructions: String,
        time: String,
        imgFile: File
    ) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""

        val userIdBody = RequestBody.create(MediaType.parse("text/plain"), userId)
        val titleBody = RequestBody.create(MediaType.parse("text/plain"), title)
        val ingredientsBody = RequestBody.create(MediaType.parse("text/plain"), ingredients)
        val instructionsBody = RequestBody.create(MediaType.parse("text/plain"), instructions)
        val timeBody = RequestBody.create(MediaType.parse("text/plain"), time)

        val requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val imagePart = MultipartBody.Part.createFormData("img", imgFile.name, requestFile)

        RetrofitClient.recipesApi.addRecipes(
            userIdBody, titleBody, ingredientsBody, instructionsBody, timeBody, imagePart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("AddRecipe", "Response: $responseBody")
                Log.d("AddRecipe", "Error: $errorBody")

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
                    Log.e("AddRecipe", "JSON error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AddRecipe", "Request Failed: ${t.message}")
                _saveStatus.value = false
            }
        })
    }

    override fun addLike(recipeId: Int) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""

        RetrofitClient.recipesApi.addLike(userId,recipeId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("AddLike", "Response: $responseBody")
                Log.d("AddLike", "Error: $errorBody")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AddRecipe", "Request Failed: ${t.message}")
            }
        })
    }

    override fun getUserRecipes() {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""
        Log.d("DEBUG_API", "userId: $userId")

        RetrofitClient.recipesApi.getUserRecipes(userId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("DEBUG_API", "Đã vào onResponse Recipe")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)
                        if (jsonObject.getBoolean("success")) {
                            val listArray = jsonObject.getJSONArray("data")
                            val recipeList = mutableListOf<Recipe>()

                            for (i in 0 until listArray.length()) {
                                val item = listArray.getJSONObject(i)
                                val recipe = Recipe(
                                    id = item.getInt("id"),
                                    title = item.getString("title"),
                                    description = item.optString("description", ""),
                                    ingredients = item.optString("ingredients", null.toString()),
                                    instructions = item.optString("instructions", null.toString()),
                                    imageUrl = item.optString("img", null.toString()),
                                    likes = item.optInt("likes", 0),
                                    timeMinutes = item.optInt("time_minutes", 0),
                                    userId = item.optInt("user_id")
                                )
                                recipeList.add(recipe)
                            }

                            // Ví dụ: cập nhật LiveData
                            _userRecipes.postValue(recipeList)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching recipes: ${t.message}")
            }
        })

    }

    override fun getRecipe(page: Int, limit: Int) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""
        Log.d("DEBUG_API", "userId: $userId")

        RetrofitClient.recipesApi.getRecipes(userId,page,limit).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("DEBUG_API", "Đã vào onResponse Recipe")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)
                        if (jsonObject.getBoolean("success")) {
                            val listArray = jsonObject.getJSONArray("data")
                            val recipeList = mutableListOf<Recipe>()

                            for (i in 0 until listArray.length()) {
                                val item = listArray.getJSONObject(i)
                                val recipe = Recipe(
                                    id = item.getInt("id"),
                                    title = item.getString("title"),
                                    description = item.optString("description", ""),
                                    ingredients = item.optString("ingredients", null.toString()),
                                    instructions = item.optString("instructions", null.toString()),
                                    imageUrl = item.optString("img", null.toString()),
                                    likes = item.optInt("likes", 0),
                                    timeMinutes = item.optInt("time_minutes", 0),
                                    userId = item.optInt("user_id"),
                                    isLiked = item.optBoolean("is_like", false)
                                )
                                recipeList.add(recipe)
                            }
                            val currentList = _recipes.value?.toMutableList() ?: mutableListOf()
                            currentList.addAll(recipeList)
                            _recipes.postValue(currentList)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching recipes: ${t.message}")
            }
        })
    }

    override fun searchRecipe(keyword: String, page: Int, limit: Int) {
        val sharedPref = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userId = sharedPref.getString("userID", "") ?: ""
        RetrofitClient.recipesApi.searchRecipes(userId,keyword,page,limit).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("DEBUG_API", "Đã vào onResponse Recipe")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val json = it.string()
                        val jsonObject = JSONObject(json)
                        if (jsonObject.getBoolean("success")) {
                            val listArray = jsonObject.getJSONArray("data")
                            val recipeList = mutableListOf<Recipe>()

                            for (i in 0 until listArray.length()) {
                                val item = listArray.getJSONObject(i)
                                val recipe = Recipe(
                                    id = item.getInt("id"),
                                    title = item.getString("title"),
                                    description = item.optString("description", ""),
                                    ingredients = item.optString("ingredients", null.toString()),
                                    instructions = item.optString("instructions", null.toString()),
                                    imageUrl = item.optString("img", null.toString()),
                                    likes = item.optInt("likes", 0),
                                    timeMinutes = item.optInt("time_minutes", 0),
                                    userId = item.optInt("user_id"),
                                    isLiked = item.optBoolean("is_like", false)
                                )
                                recipeList.add(recipe)
                            }
                            val currentList = _searchRecipes.value?.toMutableList() ?: mutableListOf()
                            currentList.addAll(recipeList)
                            _searchRecipes.postValue(currentList)
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("API_ERROR", "Error fetching recipes: ${t.message}")
            }
        })
    }

    fun removeSearchList(){
        _searchRecipes.postValue(emptyList())
    }

    override fun editRecipe(
        itemId: Int,
        title: String,
        ingredients: String,
        instructions: String,
        time: String,
        imgFile: File
    ) {
        val titleBody = RequestBody.create(MediaType.parse("text/plain"), title)
        val ingredientsBody = RequestBody.create(MediaType.parse("text/plain"), ingredients)
        val instructionsBody = RequestBody.create(MediaType.parse("text/plain"), instructions)
        val timeBody = RequestBody.create(MediaType.parse("text/plain"), time)
        val idBody =RequestBody.create(MediaType.parse("text/plain"), itemId.toString())

        val requestFile = RequestBody.create(MediaType.parse("image/*"), imgFile)
        val imagePart = MultipartBody.Part.createFormData("img", imgFile.name, requestFile)

        RetrofitClient.recipesApi.editRecipes(
            idBody,
            titleBody,
            ingredientsBody,
            instructionsBody,
            timeBody,
            imagePart
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("EditRecipe", "Response: $responseBody")
                Log.d("EditRecipe", "Error: $errorBody")

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
                    Log.e("EditRecipe", "JSON error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("EditRecipe", "Request Failed: ${t.message}")
                _saveStatus.value = false
            }
        })
    }

    override fun deleteRecipe(itemId: Int) {
        RetrofitClient.recipesApi.deleteRecipe(itemId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("AddRecipe", "Response: $responseBody")
                Log.d("AddRecipe", "Error: $errorBody")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("AddRecipe", "Request Failed: ${t.message}")
            }
        })
    }

    override fun editRecipe(
        itemId: Int,
        title: String,
        ingredients: String,
        instructions: String,
        time: String
    ) {
        RetrofitClient.recipesApi.editRecipes(
            itemId,
            title,
            ingredients,
            instructions,
            time
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBody = response.body()?.string()
                val errorBody = response.errorBody()?.string()

                Log.d("EditRecipe", "Response: $responseBody")
                Log.d("EditRecipe", "Error: $errorBody")

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
                    Log.e("EditRecipe", "JSON error: ${e.message}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("EditRecipe", "Request Failed: ${t.message}")
                _saveStatus.value = false
            }
        })
    }


}