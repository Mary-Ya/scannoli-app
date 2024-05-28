package com.maryya.ingredible.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maryya.ingredible.data.entity.Ingredient


@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertItem(ingredient: Ingredient)

    @Query("SELECT * FROM Ingredient WHERE listOwnerId = :listId")
    fun getItemsForList(listId: Int): List<Ingredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ingredient: Ingredient)

    @Delete
    fun deleteItem(ingredient: Ingredient)

}