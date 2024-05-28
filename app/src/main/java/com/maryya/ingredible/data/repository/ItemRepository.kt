import com.maryya.ingredible.data.dao.ItemListDao
import com.maryya.ingredible.data.entity.IngredientList

class ItemRepository(private val itemListDao: ItemListDao) {

    // Get all item lists
    suspend fun getAllItemLists(): List<IngredientList> {
        // Assuming itemListDao.getAllLists() is a suspend function that fetches data from the database
        return itemListDao.getAllLists()
    }

    // Insert a new item list
    suspend fun insertItemList(itemList: IngredientList) {
        itemListDao.insertList(itemList)
    }

    // Update an existing item list
    suspend fun updateItemList(itemList: IngredientList) {
        itemListDao.updateList(itemList)
    }

//     Delete an item list
    suspend fun deleteItemList(itemList: IngredientList) {
        itemListDao.deleteList(itemList)
    }

    // Additional operations as needed...
}