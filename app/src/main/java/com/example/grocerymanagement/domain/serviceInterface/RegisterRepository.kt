package com.example.grocerymanagement.domain.serviceInterface

import com.example.grocerymanagement.domain.model.CreateUserReq

interface RegisterRepository {
    fun registerUser(user: CreateUserReq)
}