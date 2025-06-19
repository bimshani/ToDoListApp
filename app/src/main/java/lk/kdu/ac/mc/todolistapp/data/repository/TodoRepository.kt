package lk.kdu.ac.mc.todolistapp.data.repository

import androidx.lifecycle.LiveData
import lk.kdu.ac.mc.todolistapp.data.database.TodoDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItem
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoList

class TodoRepository(private val todoDao: TodoDao) {

    fun getAllLists(): LiveData<List<TodoList>> = todoDao.getAllLists()

    fun getItemsByListId(listId: Long): LiveData<List<TodoItem>> = todoDao.getItemsByListId(listId)

    suspend fun insertList(todoList: TodoList): Long = todoDao.insertList(todoList)

    suspend fun updateList(todoList: TodoList) = todoDao.updateList(todoList)

    suspend fun deleteList(todoList: TodoList) = todoDao.deleteList(todoList)

    suspend fun getListById(listId: Long): TodoList? = todoDao.getListById(listId)

    suspend fun insertItem(todoItem: TodoItem): Long = todoDao.insertItem(todoItem)

    suspend fun updateItem(todoItem: TodoItem) = todoDao.updateItem(todoItem)

    suspend fun deleteItem(todoItem: TodoItem) = todoDao.deleteItem(todoItem)

    suspend fun updateItemPosition(itemId: Long, newPosition: Int) =
        todoDao.updateItemPosition(itemId, newPosition)

    suspend fun searchLists(searchQuery: String): List<TodoList> = todoDao.searchLists(searchQuery)

    suspend fun searchItems(searchQuery: String): List<TodoItem> = todoDao.searchItems(searchQuery)
}