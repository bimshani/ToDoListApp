package lk.kdu.ac.mc.todolistapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_lists")
data class TodoListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val itemCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
