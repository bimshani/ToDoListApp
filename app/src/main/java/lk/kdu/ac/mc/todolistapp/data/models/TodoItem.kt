package lk.kdu.ac.mc.todolistapp.data.models

data class TodoItem(
    val id: Long = 0,
    val listId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
