package com.example.grocerymanagement.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.grocerymanagement.domain.model.Product
import com.example.grocerymanagement.data.repository.ProductRepositoryImpl
import com.example.grocerymanagement.domain.model.ProductInfo


class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepositoryImpl(application.applicationContext)
    val product: LiveData<List<Product>> = repository.products

    // Get product from server by barcode
    fun getProductFromServer(barcode: String): LiveData<ProductInfo?> {
        return repository.getProductFromServer(barcode)
    }


    fun getProductInInvent() {
        repository.getProducts()
    }

    // Delete product from inventory
    fun deleteProductInInvent(productId: String) {
        repository.deleteProductFromInventory(productId)
    }
}
