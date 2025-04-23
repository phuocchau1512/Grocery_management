package com.example.grocerymanagement.domain.model

import android.os.Parcel
import android.os.Parcelable


data class Product(
    val id: Int,
    val name: String,
    val barcode: String,
    val img: String, // Đường dẫn ảnh
    val description: String,
    val quantity: Int,
    val note: String,
    var price: Int = 0,
    val is_private: Int
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
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
        parcel.writeInt(quantity)
        parcel.writeString(note)
        parcel.writeInt(price)
        parcel.writeInt(is_private)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }

        fun coverInfo(productInfo: ProductInfo): Product {
            return Product(productInfo.id,productInfo.name,productInfo.barcode,productInfo.img,productInfo.description,1,"",productInfo.price,productInfo.isPrivate)
        }

    }

}


