package com.example.kelineyt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.CartProduct
import com.example.kelineyt.data.Product
import com.example.kelineyt.util.FirebaseCommon
import com.example.kelineyt.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {
    private val _selectionColor = MutableStateFlow<Int?>(null)
    val selectionColor = _selectionColor.asStateFlow()

    private val _selectionSize = MutableStateFlow<String?>(null)
    val selectionSize = _selectionSize.asStateFlow()

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    private val _snackbar = MutableSharedFlow<String>()
    val snackbar = _snackbar.asSharedFlow()

    fun setSelectionColor(color: Int?) {
        viewModelScope.launch {
            _selectionColor.emit(color)
        }
    }

    fun setSelectionSize(size: String?) {
        viewModelScope.launch {
            _selectionSize.emit(size)
        }
    }

    fun addUpdateProductInCart(addProduct: Product) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        val cartProduct = CartProduct(addProduct, 1, selectionColor.value, selectionSize.value)
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id)
            .whereEqualTo("selectedColor", cartProduct.selectedColor)
            .whereEqualTo("selectedSize", cartProduct.selectedSize).get()
            .addOnSuccessListener {

                it.documents.let {
                    if (it.isEmpty()) { //Add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        product?.quantity = 1 //to make sure the condition correct
                        if (product == cartProduct) { //Increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId)
                        } else { //Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addedProduct, e ->
            viewModelScope.launch {
                if (e == null) {
                    _addToCart.emit(Resource.Success(addedProduct))
                } else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) { increasedProduct, e ->
            viewModelScope.launch {
                if (e == null) {
                    Log.d("TAG","new quantity = ${increasedProduct?.quantity}")
                    _addToCart.emit(Resource.Success(increasedProduct))
                } else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    fun showSnackbar(msg: String) {
        viewModelScope.launch {
            _snackbar.emit(msg)
        }
    }
}