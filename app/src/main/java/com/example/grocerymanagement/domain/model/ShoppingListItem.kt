package com.example.grocerymanagement.domain.model

import android.os.Parcel
import android.os.Parcelable

data class ShoppingListItem(
    val id: Int,
    val name: String,
    val isFavorite: Int,
    val itemCount: Int,
    val createdAt: String,
    val updatedAt: String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(isFavorite)
        parcel.writeInt(itemCount)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingListItem> {
        override fun createFromParcel(parcel: Parcel): ShoppingListItem {
            return ShoppingListItem(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingListItem?> {
            return arrayOfNulls(size)
        }
    }
}

