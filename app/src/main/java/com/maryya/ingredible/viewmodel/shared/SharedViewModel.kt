package com.maryya.ingredible.viewmodel.shared

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
import com.maryya.ingredible.data.db.AppDatabase
import com.maryya.ingredible.data.entity.Ingredient
import com.maryya.ingredible.data.entity.IngredientList
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SharedViewModel(app: Application) : AndroidViewModel(app) {
    private val db = AppDatabase.getDatabase(app)
    private val itemDao = db.itemDao()
    private val itemListDao = db.itemListDao()

    private val _listsLiveData = MutableLiveData<List<IngredientList>>()
    val listsLiveData: LiveData<List<IngredientList>> = _listsLiveData

    // LiveData to observe items
    private val _itemsLiveData = MutableLiveData<List<Ingredient>>()
    val itemsLiveData: LiveData<List<Ingredient>> = _itemsLiveData


    val itemRepository = ItemRepository(itemListDao)
    // Example function to insert an item with logging
    fun updateList(newItem: String) {
        itemList.add(newItem)
        viewModelScope.launch {
            Log.d("Debug111", "Before calling getAllLists")
            try {
                withContext(Dispatchers.IO) {
                    val ingredient = Ingredient(name = newItem, listOwnerId=selectedListId)
                    itemDao.insertItem(ingredient)
                }
                withContext(Dispatchers.Main) {
                    loadItemsForSelectedList()
                }
            } catch  (e: Exception) {
                Log.e("DatabaseError", "Error accessing database", e)
            }
        }
    }

    var selectedListId by mutableStateOf(0)
        private set
    // Change the method name to avoid the clash
    fun updateSelectedListId(id: Int) {
        selectedListId = id
        loadItemsForSelectedList()
    }

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
    private val _allItemLists = MutableLiveData<List<IngredientList>>()
    val allItemLists: LiveData<List<IngredientList>> = _allItemLists

    enum class LoadListOperation {
        CREATE_NEW,
        POPULATE_EXISTING,
        LOAD_EXISTING
    }

    private fun loadItemsForSelectedList() = viewModelScope.launch(Dispatchers.IO) {
        try {
            val items = itemDao.getItemsForList(selectedListId)
            withContext(Dispatchers.Main) {
                _itemsLiveData.value = items
            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error loading items", e)
        }
    }

    fun removeItemFromList(ingredient: Ingredient) = viewModelScope.launch(Dispatchers.IO) {
        try {
            itemDao.deleteItem(ingredient)
            loadItemsForSelectedList() // Refresh items list
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error removing item", e)
        }
    }
    private fun loadList() = viewModelScope.launch(Dispatchers.IO) {
        Log.d("Debug111", "Before calling getAllLists")
        try {
            var lists = itemListDao.getAllLists()

            val operation: LoadListOperation
            val itemsForSelectedList: List<Ingredient>?

            if (lists.isEmpty()) {
                // If no lists in DB, create a new one and prepare its ID
                val newListId = itemListDao.insertList(IngredientList(name = "Default List", isActive = true))
                operation = LoadListOperation.CREATE_NEW
                // Since this is a new list, there are no items yet
                itemsForSelectedList = null
            } else {
                // Lists are present, use the first list's ID as selected
                val selectedListIdTemp = lists[0].listId
                val items = itemDao.getItemsForList(selectedListIdTemp)
                operation = if (items.isEmpty()) {
                    // If the selected list is empty, populate it with initial items
                    initialList.forEach { item ->
                        itemDao.insertItem(Ingredient(name = item, listOwnerId = selectedListIdTemp))
                    }
                    LoadListOperation.POPULATE_EXISTING
                } else {
                    LoadListOperation.LOAD_EXISTING
                }
                // Fetch the items for the selected list
                itemsForSelectedList = itemDao.getItemsForList(selectedListIdTemp)
            }

            // Switch to Main thread to update UI
            withContext(Dispatchers.Main) {
                when (operation) {
                    LoadListOperation.CREATE_NEW -> {
                        // Refresh the list to include the newly created one
                        lists = itemListDao.getAllLists()
                        selectedListId = lists.first().listId
                    }
                    LoadListOperation.POPULATE_EXISTING, LoadListOperation.LOAD_EXISTING -> {
                        selectedListId = lists.first().listId
                        itemList.clear()
                        itemsForSelectedList?.let { itemList.addAll(it.map { item -> item.name }) }
                    }
                }
                _listsLiveData.postValue(lists)
                loadItemsForSelectedList()

            }
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error accessing database", e)
        }
    }

    fun addNewList(listName: String) = viewModelScope.launch(Dispatchers.IO) {
        val newList = IngredientList(name = listName, isActive = true)
        val id = itemListDao.insertList(newList)
        Log.d("SharedViewModel", "New list inserted with ID: $id")
        // Fetch the latest lists to update LiveData
        val updatedLists = itemListDao.getAllLists()
        withContext(Dispatchers.Main) {
            _listsLiveData.value = updatedLists
        }
    }

    fun updateItemListActiveState(itemList: IngredientList, isActive: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        val updatedList = itemList.copy(isActive = isActive)
        itemListDao.updateList(updatedList)
        val updatedLists = itemListDao.getAllLists()
        withContext(Dispatchers.Main) {
            _listsLiveData.value = updatedLists
        }
    }

    init {
        loadList()
        initializeColorMap()
    }
}