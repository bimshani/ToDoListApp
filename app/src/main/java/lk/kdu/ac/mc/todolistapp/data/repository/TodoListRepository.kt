package lk.kdu.ac.mc.todolistapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import lk.kdu.ac.mc.todolistapp.data.database.dao.TodoListDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.models.TodoList

class TodoListRepository(private val todoListDao: TodoListDao) {
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
