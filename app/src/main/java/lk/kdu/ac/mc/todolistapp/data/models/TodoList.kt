package lk.kdu.ac.mc.todolistapp.data.models

data class TodoList(
    val id: Long = 0,
    val title: String,
    val itemCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
