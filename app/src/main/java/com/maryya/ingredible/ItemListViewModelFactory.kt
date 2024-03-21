package com.maryya.ingredible

import ItemListViewModel
import ItemRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ItemListViewModelFactory(private val repository: ItemRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}