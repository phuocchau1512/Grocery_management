package com.example.grocerymanagement.domain.model

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val id: Int,
    val title: String,
    val description: String?,
    val ingredients: String?,
    val instructions: String?,
    val imageUrl: String?,
    var likes: Int = 0,
    val timeMinutes: Int = 0,
    val userId: Int?,
    var isLiked: Boolean = false
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(ingredients)
        parcel.writeString(instructions)
        parcel.writeString(imageUrl)
        parcel.writeInt(likes)
        parcel.writeInt(timeMinutes)
        parcel.writeValue(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}