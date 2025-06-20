package lk.kdu.ac.mc.todolistapp.datasource.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import lk.kdu.ac.mc.todolistapp.data.models.TodoList

@Entity(
    tableName = "todo_items",
    foreignKeys = [ForeignKey(
        entity = TodoList::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("listId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class TodoEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val listId: Long,
    val description: String,
    val isCompleted: Boolean = false,
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)