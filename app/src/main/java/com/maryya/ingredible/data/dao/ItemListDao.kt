package com.maryya.ingredible.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maryya.ingredible.data.entity.ItemList

@Dao
interface ItemListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(itemList: ItemList): Long // Returns the new row ID

    @Query("SELECT * FROM ItemList")
    public fun getAllLists(): List<ItemList>

//     Method to update an existing item list
    @Update
    fun updateList(itemList: ItemList): Int

    // Method to delete an existing item list
    @Delete
    fun deleteList(itemList: ItemList): Int
}
