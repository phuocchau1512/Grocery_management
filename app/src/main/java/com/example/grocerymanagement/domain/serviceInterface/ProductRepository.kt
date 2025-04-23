package com.example.grocerymanagement.domain.serviceInterface

import androidx.lifecycle.LiveData
import com.example.grocerymanagement.domain.model.ProductInfo
import java.io.File

interface ProductRepository {


    fun addProductToInvent(name: String, barcode: String, description: String, quantity: String, note: String, imgFile: File)
    fun addProductToInvent(productId: Int, quantity:String, note:String)
    fun editProductToInvent(productId: Int, name: String, barcode: String, description: String, quantity: String, note: String, imgFile: File?)
    fun editPublicProductToInvent(productId: Int, quantity:String, note:String)
    fun getProductFromServer(barcode:String) : LiveData<ProductInfo?>
    fun getProducts()
    fun deleteProductFromInventory(productId: String)
}