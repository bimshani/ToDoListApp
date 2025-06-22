package lk.kdu.ac.mc.todolistapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// This represents a whole todo list in the app
// Each list can contain multiple tasks inside it
@Entity(tableName = "todo_lists")
data class TodoListEntity(
    // Each list gets its own unique ID number
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // The name of the list
    val title: String,

    // Keeps track of how many tasks are in this list
    val itemCount: Int = 0,

    // When this list was created
    val createdAt: Long = System.currentTimeMillis()
)
