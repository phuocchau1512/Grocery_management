package com.example.grocerymanagement.presentation.adapter.adapterItem

data class ShoppingList(
    val id: String,
    val name: String,
    val dateCreated: String,
    val itemCount: Int,
    val isFavorite: Boolean = false
)
