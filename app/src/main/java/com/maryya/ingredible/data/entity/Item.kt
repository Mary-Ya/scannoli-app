package com.maryya.ingredible.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = ItemList::class,
            parentColumns = ["listId"],
            childColumns = ["listOwnerId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)

data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val listOwnerId: Int // This associates an Item with an ItemList
)
