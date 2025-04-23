package com.example.grocerymanagement.presentation.adapter.adapterItem

import android.os.Parcel
import android.os.Parcelable


data class ShoppingItemProduct(
    val id: Int,
    val shoppingListId: Int,
    val productId: Int,
    val quantity: Int,
    var isChecked: Int,
    val productName: String,
    val barcode: String?,
    val img: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(shoppingListId)
        parcel.writeInt(productId)
        parcel.writeInt(quantity)
        parcel.writeInt(isChecked)
        parcel.writeString(productName)
        parcel.writeString(barcode)
        parcel.writeString(img)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingItemProduct> {
        override fun createFromParcel(parcel: Parcel): ShoppingItemProduct {
            return ShoppingItemProduct(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingItemProduct?> {
            return arrayOfNulls(size)
        }
    }
}

