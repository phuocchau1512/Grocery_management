package com.example.grocerymanagement.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.grocerymanagement.domain.model.CreateUserReq
import com.example.grocerymanagement.data.repository.RegisterRepositoryImpl

class RegisterViewModel : ViewModel() {
    private val repository = RegisterRepositoryImpl()
    val registerStatus: LiveData<Pair<Boolean, String>> = repository.registerStatus

    fun registerUser(user: CreateUserReq) {
        repository.registerUser(user)
    }
}