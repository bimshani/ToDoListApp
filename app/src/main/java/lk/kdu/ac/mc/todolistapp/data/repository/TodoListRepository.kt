package lk.kdu.ac.mc.todolistapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import lk.kdu.ac.mc.todolistapp.data.database.dao.TodoListDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem

// This handles everything related to performing,
// - Creating new lists
// - Adding tasks to lists
// - Updating or deleting lists and tasks
// - Searching through lists

class TodoListRepository(private val todoListDao: TodoListDao) {
    // ==== List Operations ====
    // These functions help manage entire lists

    // Get all todo lists
    fun getAllLists(): LiveData<List<TodoList>> {
        return todoListDao.getAllLists().map { entities ->
            entities.map { it.toModel() }
        }
    }

    // Create a new list with a title
    suspend fun insertList(title: String): Long {
        val entity = TodoListEntity(title = title)
        return todoListDao.insert(entity)
    }

    // Update a list's information
    suspend fun updateList(todoList: TodoList) {
        todoListDao.update(todoList.toEntity())
    }

    // Delete a list (and all its tasks)
    suspend fun deleteList(todoList: TodoList) {
        todoListDao.delete(todoList.toEntity())
    }

    // Search through lists by their titles
    fun searchLists(query: String): LiveData<List<TodoList>> {
        return todoListDao.searchLists(query).map { entities ->
            entities.map { it.toModel() }
        }
    }

    // ==== Task Operations ====
    // These functions help manage tasks within lists

    // Get all tasks in a specific list
    fun getTasksForList(listId: Long): LiveData<List<TodoItem>> {
        return todoListDao.getTasksForList(listId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    // Add a new task to a list
    // Also updates the task count for that list
    suspend fun insertTask(task: TodoItem) {
        val maxPosition = todoListDao.getMaxPositionForList(task.listId) ?: -1
        val taskWithPosition = task.toEntity().copy(position = maxPosition + 1)
        todoListDao.insertTask(taskWithPosition)
        updateListItemCount(task.listId)
    }

    // Update a task's information
    suspend fun updateTask(task: TodoItem) {
        todoListDao.updateTask(task.toEntity())
    }

    // Delete a task and update the list's task count
    suspend fun deleteTask(task: TodoItem) {
        todoListDao.deleteTask(task.toEntity())
        updateListItemCount(task.listId)
    }


    // Update how many tasks are in a list
    private suspend fun updateListItemCount(listId: Long) {
        val count = todoListDao.getTaskCountForList(listId)
        todoListDao.updateListItemCount(listId, count)
    }

    // Find a specific task by its ID
    suspend fun getTaskById(taskId: Long): TodoItem? {
        return todoListDao.getTaskById(taskId)?.toModel()
    }

    // ==== Statistics Functions ====
    // These help keep track of task progress

    // Count how many tasks are still not done
    suspend fun getPendingTasksCount(): Int {
        return todoListDao.getPendingTasksCount()
    }

    // Count how many tasks you've completed
    suspend fun getCompletedTasksCount(): Int {
        return todoListDao.getCompletedTasksCount()
    }

    // ==== Conversion functions ====

    // Because room database (which stores data) needs things in a specific format (Entities),
    // but app needs them in a different format (Models) that's easier to work with.

    private fun TodoItemEntity.toModel() = TodoItem(
        id = id,
        listId = listId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        position = position,
        createdAt = createdAt
    )


    private fun TodoItem.toEntity() = TodoItemEntity(
        id = id,
        listId = listId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        position = position,
        createdAt = createdAt
    )


    private fun TodoListEntity.toModel() = TodoList(
        id = id,
        title = title,
        itemCount = itemCount,
        createdAt = createdAt
    )

    // And this converts an app list to a version we can save in the database
    // Example: AppList("Shopping") -> DatabaseList("Shopping")
    private fun TodoList.toEntity() = TodoListEntity(
        id = id,
        title = title,
        itemCount = itemCount,
        createdAt = createdAt
    )
}
