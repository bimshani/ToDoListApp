package lk.kdu.ac.mc.todolistapp.datasource.datamanager

import androidx.lifecycle.LiveData
import lk.kdu.ac.mc.todolistapp.datasource.database.TodoDataAccess
import lk.kdu.ac.mc.todolistapp.datasource.database.entities.TodoEntry
import lk.kdu.ac.mc.todolistapp.datasource.database.entities.TodoList

class TodoDataRepository(private val todoDataAccess: TodoDataAccess) {

    fun getAllLists(): LiveData<List<TodoList>> = todoDataAccess.getAllLists()

    fun getItemsByListId(listId: Long): LiveData<List<TodoEntry>> = todoDataAccess.getItemsByListId(listId)

    suspend fun insertList(todoList: TodoList): Long = todoDataAccess.insertList(todoList)

    suspend fun updateList(todoList: TodoList) = todoDataAccess.updateList(todoList)

    suspend fun deleteList(todoList: TodoList) = todoDataAccess.deleteList(todoList)

    suspend fun getListById(listId: Long): TodoList? = todoDataAccess.getListById(listId)

    suspend fun insertItem(todoEntry: TodoEntry): Long = todoDataAccess.insertItem(todoEntry)

    suspend fun updateItem(todoEntry: TodoEntry) = todoDataAccess.updateItem(todoEntry)

    suspend fun deleteItem(todoEntry: TodoEntry) = todoDataAccess.deleteItem(todoEntry)

    suspend fun updateItemPosition(itemId: Long, newPosition: Int) =
        todoDataAccess.updateItemPosition(itemId, newPosition)

    suspend fun searchLists(searchQuery: String): List<TodoList> = todoDataAccess.searchLists(searchQuery)

    suspend fun searchItems(searchQuery: String): List<TodoEntry> = todoDataAccess.searchItems(searchQuery)
}