package com.example.grocerymanagement.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.grocerymanagement.data.repository.ShoppingRepositoryImpl
import com.example.grocerymanagement.domain.model.ProductInfo
import com.example.grocerymanagement.domain.model.ShoppingListItem
import com.example.grocerymanagement.presentation.adapter.adapterItem.ShoppingItemProduct
import java.io.File

class ShoppingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShoppingRepositoryImpl(application.applicationContext)
    val saveStatus: LiveData<Boolean> = repository.saveStatus
    val shoppingList: LiveData<List<ShoppingListItem>> = repository.shoppingLists
    val shoppingListItem: LiveData<List<ShoppingItemProduct>> = repository.shoppingListItems

    fun addNewList(name: String){
        repository.addNewList(name)
    }

    fun addProductToList(listId: String, name: String, barcode: String, description: String, quantity:String, note:String, imgFile: File) {
        repository.addProductToList(listId, name, barcode, description, quantity , note, imgFile)
    }

    fun addProductToList(productId: Int,quantity: Int) {
        repository.addProductToList(productId,quantity)
    }

    fun addFav(shoppingListId: Int){
        repository.addFav(shoppingListId)
    }

    fun addCheck(itemId:Int){
        repository.addCheck(itemId)
    }

    fun getShoppingLists(){
        repository.getShoppingLists()
    }

    fun deleteShoppingList(shoppingListId: Int){
        repository.deleteShoppingList(shoppingListId)
    }

    fun getShoppingItem(shoppingListItem: Int){
        repository.getShoppingItem(shoppingListItem)
    }

    fun getProductFromServer(barcode: String): LiveData<ProductInfo?> {
        return repository.getProductFromServer(barcode)
    }

    fun saveProductToShoppingList(shoppingListId: Int ,productId:Int, quantity:Int){
        repository.saveProductToList(shoppingListId,productId,quantity)
    }

    fun deleteShoppingListItem(itemId:Int,productId: Int){
        repository.deleteShoppingItem(itemId, productId)
    }




}