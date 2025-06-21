package lk.kdu.ac.mc.todolistapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import lk.kdu.ac.mc.todolistapp.data.database.dao.TodoListDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem

class TodoListRepository(private val todoListDao: TodoListDao) {
    // List operations
    fun getAllLists(): LiveData<List<TodoList>> {
        return todoListDao.getAllLists().map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertList(title: String) {
        val entity = TodoListEntity(title = title)
        todoListDao.insert(entity)
    }

    suspend fun updateList(todoList: TodoList) {
        todoListDao.update(todoList.toEntity())
    }

    suspend fun deleteList(todoList: TodoList) {
        todoListDao.delete(todoList.toEntity())
    }

    fun searchLists(query: String): LiveData<List<TodoList>> {
        return todoListDao.searchLists(query).map { entities ->
            entities.map { it.toModel() }
        }
    }

    // Task operations
    fun getTasksForList(listId: Long): LiveData<List<TodoItem>> {
        return todoListDao.getTasksForList(listId).map { entities ->
            entities.map { it.toModel() }
        }
    }

    suspend fun insertTask(task: TodoItem) {
        todoListDao.insertTask(task.toEntity())
        updateListItemCount(task.listId)
    }

    suspend fun updateTask(task: TodoItem) {
        todoListDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: TodoItem) {
        todoListDao.deleteTask(task.toEntity())
        updateListItemCount(task.listId)
    }

    suspend fun getPendingTasksCount(): Int {
        return todoListDao.getPendingTasksCount()
    }

    suspend fun getCompletedTasksCount(): Int {
        return todoListDao.getCompletedTasksCount()
    }

    private suspend fun updateListItemCount(listId: Long) {
        val count = todoListDao.getTaskCountForList(listId)
        todoListDao.updateListItemCount(listId, count)
    }

    // Entity conversion methods
    private fun TodoItemEntity.toModel() = TodoItem(
        id = id,
        listId = listId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt
    )

    private fun TodoItem.toEntity() = TodoItemEntity(
        id = id,
        listId = listId,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt
    )

    private fun TodoListEntity.toModel() = TodoList(
        id = id,
        title = title,
        itemCount = itemCount,
        createdAt = createdAt
    )

    private fun TodoList.toEntity() = TodoListEntity(
        id = id,
        title = title,
        itemCount = itemCount,
        createdAt = createdAt
    )
}
