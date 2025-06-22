package lk.kdu.ac.mc.todolistapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// This represents a whole list in app
// This class is used when working with lists in the app's memory
@Entity(tableName = "todo_lists")
data class TodoList(
    // Every list gets its own unique ID
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // The name give to list
    val title: String,

    // How many tasks are in this list
    val itemCount: Int = 0,

    // When created this list
    val createdAt: Long = System.currentTimeMillis()
)
