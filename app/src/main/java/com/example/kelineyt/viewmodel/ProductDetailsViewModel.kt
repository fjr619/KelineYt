package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(): ViewModel() {
    private val _selectionColor = MutableStateFlow<Int?>(null)
    val selectionColor = _selectionColor.asStateFlow()

    private val _selectionSize = MutableStateFlow<String?>(null)
    val selectionSize = _selectionSize.asStateFlow()

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
}