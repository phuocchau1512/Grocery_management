package com.example.grocerymanagement.domain.serviceInterface

import java.io.File

interface RecipesRepository {

    fun addRecipes(title: String, ingredients: String, instructions: String, time: String, imgFile: File)
    fun addLike(recipeId: Int)
    fun getUserRecipes()
    fun getRecipe(page: Int,limit: Int)
    fun searchRecipe(keyword:String, page: Int, limit: Int)
    fun editRecipe(itemId: Int, title: String, ingredients: String, instructions: String, time: String)
    fun editRecipe(itemId: Int, title: String, ingredients: String, instructions: String, time: String,imgFile: File)
    fun deleteRecipe(itemId: Int)
}