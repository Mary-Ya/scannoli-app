package com.maryya.ingredible.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.maryya.ingredible.data.entity.IngredientList
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(itemList: IngredientList): Long // Returns the new row ID

    @Query("SELECT * FROM IngredientList")
    public fun getAllLists(): List<IngredientList>

//     Method to update an existing item list
    @Update
    fun updateList(itemList: IngredientList): Int

    // Method to delete an existing item list
    @Delete
    fun deleteList(itemList: IngredientList): Int

    @Query("SELECT * FROM IngredientList")
    fun getAllListsFlow(): Flow<List<IngredientList>>
}
