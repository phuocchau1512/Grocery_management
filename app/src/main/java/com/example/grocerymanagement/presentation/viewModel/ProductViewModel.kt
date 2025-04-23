package com.example.grocerymanagement.presentation.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.grocerymanagement.data.repository.ProductRepositoryImpl
import java.io.File

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductRepositoryImpl(application.applicationContext)
    val saveStatus: LiveData<Boolean> = repository.saveStatus

    fun addProductToInvent(name: String, barcode: String, description: String, quantity:String, note:String, imgFile: File) {
        repository.addProductToInvent(name, barcode, description, quantity , note, imgFile)
    }

    fun addProductToInvent(productId: Int, quantity:String, note:String) {
        repository.addProductToInvent(productId,quantity , note)
    }

    fun editProductToInvent(productId: Int, name: String, barcode: String, description: String, quantity:String, note:String, imgFile: File?) {
        repository.editProductToInvent(productId, name, barcode, description, quantity , note, imgFile)
    }

    fun editPublicProductToInvent(productId: Int, quantity:String, note:String) {
        repository.editPublicProductToInvent(productId, quantity , note)
    }

}
