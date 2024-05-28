package com.maryya.ingredible.data.repository

import com.maryya.ingredible.data.db.AppDatabase
import com.maryya.ingredible.data.entity.Ingredient
import com.maryya.ingredible.data.entity.IngredientList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ListRepository(private val database: AppDatabase) {

    private val itemListDao = database.itemListDao()
    private val itemDao = database.itemDao()

    suspend fun getAllLists(): List<IngredientList> = withContext(Dispatchers.IO) {
        itemListDao.getAllLists()
    }

    suspend fun insertList(itemList: IngredientList): Long = withContext(Dispatchers.IO) {
        itemListDao.insertList(itemList)
    }

    suspend fun updateList(itemList: IngredientList) = withContext(Dispatchers.IO) {
        itemListDao.updateList(itemList)
    }

    suspend fun getItemsForList(listId: Int): List<Ingredient> = withContext(Dispatchers.IO) {
        itemDao.getItemsForList(listId)
    }

    suspend fun insertItem(ingredient: Ingredient) = withContext(Dispatchers.IO) {
        itemDao.insertItem(ingredient)
    }

    suspend fun deleteItem(ingredient: Ingredient) = withContext(Dispatchers.IO) {
        itemDao.deleteItem(ingredient)
    }

    // Add any additional repository methods that match the operations in SharedViewModel or required by your application's logic.
}