package com.example.grocerymanagement.domain.serviceInterface

import com.example.grocerymanagement.domain.model.LoginRequest

interface LoginRepository {
    fun loginUser(user: LoginRequest)
}