package com.example.kelineyt.data

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    @set:Exclude
    @get:Exclude
    var id: String = "",
    val addressTitle: String = "",
    val fullName: String = "",
    val street: String = "",
    val phone: String = "",
    val city: String = "",
    val state: String = "",
): Parcelable