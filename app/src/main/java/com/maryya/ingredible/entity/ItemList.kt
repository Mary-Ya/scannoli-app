package com.maryya.ingredible.entity
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity
data class ItemList(
    @PrimaryKey(autoGenerate = true) val listId: Int,
    val name: String // This could be the name of the list
)
