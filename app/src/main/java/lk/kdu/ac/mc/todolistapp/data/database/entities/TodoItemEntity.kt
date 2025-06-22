package lk.kdu.ac.mc.todolistapp.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// This class represents a single task in the todo list to defines what information we store for each task in the database
@Entity(
    tableName = "todo_items",
    // This connects each task to its parent list
    // When we delete a list, all its tasks are also deleted
    foreignKeys = [
        ForeignKey(
            entity = TodoListEntity::class,
            parentColumns = ["id"],
            childColumns = ["listId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // This helps the app quickly find tasks by their list or position
    indices = [Index("listId"), Index("position")]
)
data class TodoItemEntity(
    // Each task gets a unique ID number
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // This tells us which list this task belongs to
    val listId: Long,

    // The main text of the task
    val title: String,

    // Extra details about the task (optional)
    val description: String = "",

    // Whether the task is done or not
    val isCompleted: Boolean = false,

    // Where this task appears in the list (for ordering)
    val position: Int = 0,

    // When the task was created
    val createdAt: Long = System.currentTimeMillis()
)
