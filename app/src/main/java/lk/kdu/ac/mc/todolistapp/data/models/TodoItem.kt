package lk.kdu.ac.mc.todolistapp.data.models

// This is what a task looks like in app
// This class is used when we're working with tasks in the app's memory
data class TodoItem(
    // Each task's unique ID number
    val id: Long = 0,

    // Which todo list this task belongs to
    val listId: Long,

    // The main text of the task
    val title: String,

    // Any extra notes about the task
    val description: String = "",

    // Whether you've finished this task or not
    val isCompleted: Boolean = false,

    // The task's position in the list (for ordering)
    val position: Int = 0,

    // When you created this task
    val createdAt: Long = System.currentTimeMillis()
)
