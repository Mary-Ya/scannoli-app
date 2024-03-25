package com.maryya.ingredible.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maryya.ingredible.entity.Item
import java.sql.RowId


@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(item: Item)

    @Query("SELECT * FROM Item WHERE listOwnerId = :listId")
    fun getItemsForList(listId: Int): List<Item>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: Item)

    @Delete
    fun deleteItem(item: Item)
}