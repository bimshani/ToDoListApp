package lk.kdu.ac.mc.todolistapp.datasource.database

import androidx.lifecycle.LiveData
import androidx.room.*
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.datasource.database.entities.TodoEntry

@Dao
interface TodoDataAccess {

    // TodoList operations
    @Query("SELECT * FROM todo_lists ORDER BY createdAt DESC")
    fun getAllLists(): LiveData<List<TodoList>>

    @Insert
    suspend fun insertList(todoList: TodoList): Long

    @Update
    suspend fun updateList(todoList: TodoList)

    @Delete
    suspend fun deleteList(todoList: TodoList)

    @Query("SELECT * FROM todo_lists WHERE id = :listId")
    suspend fun getListById(listId: Long): TodoList?

    // TodoItem operations
    @Query("SELECT * FROM todo_items WHERE listId = :listId ORDER BY position ASC")
    fun getItemsByListId(listId: Long): LiveData<List<TodoEntry>>

    @Insert
    suspend fun insertItem(todoEntry: TodoEntry): Long

    @Update
    suspend fun updateItem(todoEntry: TodoEntry)

    @Delete
    suspend fun deleteItem(todoEntry: TodoEntry)

    @Query("UPDATE todo_items SET position = :newPosition WHERE id = :itemId")
    suspend fun updateItemPosition(itemId: Long, newPosition: Int)

    // Search functionality
    @Query("SELECT DISTINCT todo_lists.* FROM todo_lists INNER JOIN todo_items ON todo_lists.id = todo_items.listId WHERE todo_items.description LIKE '%' || :searchQuery || '%' OR todo_lists.title LIKE '%' || :searchQuery || '%'")
    suspend fun searchLists(searchQuery: String): List<TodoList>

    @Query("SELECT * FROM todo_items WHERE description LIKE '%' || :searchQuery || '%'")
    suspend fun searchItems(searchQuery: String): List<TodoEntry>
}
