package com.example.grocerymanagement.domain.model

import android.os.Parcel
import android.os.Parcelable

data class ProductInfo(
    val id: Int,
    val name: String,
    val barcode: String,
    val img: String, // Đường dẫn ảnh
    val description: String,
    var price: Int = 0,
    val isPrivate: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(barcode)
        parcel.writeString(img)
        parcel.writeString(description)
        parcel.writeInt(price)
        parcel.writeInt(isPrivate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductInfo> {
        override fun createFromParcel(parcel: Parcel): ProductInfo {
            return ProductInfo(parcel)
        }

        override fun newArray(size: Int): Array<ProductInfo?> {
            return arrayOfNulls(size)
        }
    }

}
