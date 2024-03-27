import com.maryya.ingredible.data.dao.ItemListDao
import com.maryya.ingredible.data.entity.ItemList

class ItemRepository(private val itemListDao: ItemListDao) {

    // Get all item lists
    suspend fun getAllItemLists(): List<ItemList> {
        // Assuming itemListDao.getAllLists() is a suspend function that fetches data from the database
        return itemListDao.getAllLists()
    }

    // Insert a new item list
    suspend fun insertItemList(itemList: ItemList) {
        itemListDao.insertList(itemList)
    }

    // Update an existing item list
    suspend fun updateItemList(itemList: ItemList) {
        itemListDao.updateList(itemList)
    }

//     Delete an item list
    suspend fun deleteItemList(itemList: ItemList) {
        itemListDao.deleteList(itemList)
    }

    // Additional operations as needed...
}