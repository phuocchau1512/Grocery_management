package com.example.grocerymanagement.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.grocerymanagement.data.repository.RecipesRepositoryImpl
import com.example.grocerymanagement.domain.model.Recipe
import java.io.File

class RecipesViewModel(application: Application) : AndroidViewModel(application){

    private val repository = RecipesRepositoryImpl(application.applicationContext)
    val saveStatus: LiveData<Boolean> = repository.saveStatus
    val recipes: LiveData<List<Recipe>> = repository.recipes
    val userRecipes: LiveData<List<Recipe>> = repository.userRecipes
    val searchRecipes: LiveData<List<Recipe>> = repository.searchRecipes

    fun addRecipes(
        title: String,
        ingredients: String,
        instructions: String,
        time: String,
        imgFile: File
    ){
        repository.addRecipes(title,ingredients,instructions,time,imgFile)
    }

    fun addLike(itemId:Int){
        repository.addLike(itemId)
    }


    fun getUserRecipes() {
        repository.getUserRecipes()
    }

    fun getRecipes(page:Int, limit:Int) {
        repository.getRecipe(page,limit)
    }

    fun searchRecipes(keyword:String, page:Int, limit:Int) {
        repository.searchRecipe(keyword,page,limit)
    }



    fun editRecipe(
        itemId: Int,
        title: String,
        ingredients: String,
        instructions: String,
        time: String,
        imgFile: File
    ){
        repository.editRecipe(itemId,title,ingredients,instructions,time,imgFile)
    }

    fun editRecipe(
        itemId: Int,
        title: String,
        ingredients: String,
        instructions: String,
        time: String,
    ){
        repository.editRecipe(itemId,title,ingredients,instructions,time)
    }

    fun deleteRecipe(itemId: Int){
        repository.deleteRecipe(itemId)
    }

    fun removeSearch() {
        repository.removeSearchList()
    }

}