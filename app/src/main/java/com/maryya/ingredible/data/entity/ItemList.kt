package com.maryya.ingredible.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true) val listId: Int = 0,
    val name: String, // This could be the name of the list
    var isActive: Boolean = true
)
