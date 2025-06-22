package lk.kdu.ac.mc.todolistapp.data.models

data class TodoItem(
    val id: Long = 0,
    val listId: Long,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val position: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
