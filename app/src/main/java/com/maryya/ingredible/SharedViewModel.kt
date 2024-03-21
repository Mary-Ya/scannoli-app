package com.maryya.ingredible

import ItemRepository
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maryya.ingredible.db.AppDatabase
import com.maryya.ingredible.entity.Item
import com.maryya.ingredible.entity.ItemList
import kotlinx.coroutines.flow.flow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val itemDao = db.itemDao()
    private val itemListDao = db.itemListDao()

    val itemRepository = ItemRepository(itemListDao)
    // Example function to insert an item with logging
    fun insertItem(item: Item) = viewModelScope.launch {
        Log.d("AppDatabase", "Inserting item: ${item.name}")
        itemDao.insertItem(item)
        Log.d("AppDatabase", "Item inserted")
    }

    // Function to get items for a list, showing usage of the DAO within a ViewModel
    fun getItemsForListFlow(listId: Int) = flow {
        val items = itemDao.getItemsForList(listId) // Assuming this is a suspend function
        emit(items)
    }

    var selectedListId by mutableStateOf(0)
    var initialList = listOf(
        "mela", "mele", // Apple, Apples
        "pesca", "pesche", // Peach, Peaches
        "pera", "pere", // Pear, Pears
        "pomodoro", "pomodori", // Tomato, Tomatoes
        "arachide", "arachidi", // Peanut, Peanuts
        "mandorla", "mandorle", // Almond, Almonds
        "noce", "noci", // Walnut, Walnuts
        "nocciola", "nocciole", // Hazelnut, Hazelnuts
        "frutto a guscio", "frutta a guscio", // Tree nut, Tree nuts
        "olio di arachide", // Peanut oil
        "burro di arachidi", // Peanut butter
        "farina di mandorle", // Almond flour
        "estratto di vaniglia", // Vanilla extract
        "marzapane", // Marzipan
        "nutella" // Nutella
    )

    var itemList = mutableStateListOf<String>()
        private set

    fun resetList() {
        itemList.clear()
        itemList.addAll(initialList)
    }

    fun updateList(newItem: String) {
        itemList.add(newItem)
    }

    fun removeItemFromList(index: Int) {
        itemList.toMutableList().apply {
            removeAt(index)
        }
    }

    private var colorList = mutableStateListOf(
        Color(android.graphics.Color.parseColor("#645AD4")), // Violet
        Color(android.graphics.Color.parseColor("#7F00CC")), // Purple
        Color(android.graphics.Color.parseColor("#39B846")), // Green
        Color(android.graphics.Color.parseColor("#FFDB1F")), // Yellow
        Color(android.graphics.Color.parseColor("#1F94FF")), // Blue
    )

    // Map to store the color for each item
    private var itemColorMap = mutableStateMapOf<String, Color>()

    private fun initializeColorMap() {
        itemList.forEachIndexed { index, item ->
            itemColorMap[item] = colorList[index % colorList.size]
        }
    }

    fun getColorForItem(item: String): Color {
        return itemColorMap[item] ?: Color.Gray // Default color if not found
    }
    private val _allItemLists = MutableLiveData<List<ItemList>>()
    val allItemLists: LiveData<List<ItemList>> = _allItemLists

    private fun loadList() {
        var lists: List<ItemList> = emptyList()

        viewModelScope.launch {
            Log.d("Debug111", "Before calling getAllLists")
//            val lists = itemListDao.getAllLists()
            try {
                withContext(Dispatchers.IO) {
                    lists = itemListDao.getAllLists()

                    // Other database operations...

                    if (lists.isEmpty()) {
                        // No lists in DB, create one and use its ID as selectedListId
                        val newListId =
                            itemListDao.insertList(ItemList(name = "Default List", listId = 0))
                        selectedListId = newListId.toInt()
                        // Populate the new list with initial items
                        initialList.forEach { item ->
                            itemDao.insertItem(Item(name = item, listOwnerId = selectedListId))
                        }
                    } else {
                        // Use the first list as the default list
                        selectedListId = lists[0].listId
                        val items = itemDao.getItemsForList(selectedListId)
                        if (items.isEmpty()) {
                            // If the selected list is empty, populate it with initial items
                            initialList.forEach { item ->
                                itemDao.insertItem(Item(name = item, listOwnerId = selectedListId))
                            }
                        } else {
                            // Load items from the selected list
                            itemList.clear()
                            itemList.addAll(items.map { it.name })
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DatabaseError", "Error accessing database", e)
            }
            Log.d("Debug111", "After calling getAllLists")
        }
    }

    init {
        loadList()
        initializeColorMap()
    }
}