package com.example.grocerymanagement.domain.serviceInterface

import androidx.lifecycle.LiveData
import com.example.grocerymanagement.domain.model.ProductInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import java.io.File

interface ShoppingRepository {
    fun addNewList(name: String)
    fun addProductToList(listId: String, name: String, barcode: String, description: String, quantity:String, note:String, imgFile: File)
    fun addProductToList(productId: Int,quantity: Int)
    fun getShoppingLists()
    fun deleteShoppingList(shoppingListId: Int)
    fun deleteShoppingItem(itemId:Int,productId: Int)
    fun addFav(shoppingListId: Int)
    fun addCheck(itemId:Int)
    fun getShoppingItem(shoppingListItem: Int)
    fun getProductFromServer(barcode:String) : LiveData<ProductInfo?>
    fun saveProductToList(shoppingListId: Int, productId: Int, quantity: Int)
}