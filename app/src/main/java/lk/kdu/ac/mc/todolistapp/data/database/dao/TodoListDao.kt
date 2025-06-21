package lk.kdu.ac.mc.todolistapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity

@Dao
interface TodoListDao {
    // List operations
    @Query("SELECT * FROM todo_lists ORDER BY createdAt DESC")
    fun getAllLists(): LiveData<List<TodoListEntity>>

    @Insert
    suspend fun insert(todoList: TodoListEntity): Long

    @Update
    suspend fun update(todoList: TodoListEntity)

    @Delete
    suspend fun delete(todoList: TodoListEntity)

    @Query("SELECT * FROM todo_lists WHERE title LIKE '%' || :query || '%'")
    fun searchLists(query: String): LiveData<List<TodoListEntity>>

    // Task operations
    @Query("SELECT * FROM todo_items WHERE listId = :listId ORDER BY createdAt DESC")
    fun getTasksForList(listId: Long): LiveData<List<TodoItemEntity>>

    @Insert
    suspend fun insertTask(task: TodoItemEntity): Long

    @Update
    suspend fun updateTask(task: TodoItemEntity)

    @Delete
    suspend fun deleteTask(task: TodoItemEntity)

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 0")
    suspend fun getPendingTasksCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    suspend fun getCompletedTasksCount(): Int

    @Query("SELECT COUNT(*) FROM todo_items WHERE listId = :listId")
    suspend fun getTaskCountForList(listId: Long): Int

    @Query("UPDATE todo_lists SET itemCount = :count WHERE id = :listId")
    suspend fun updateListItemCount(listId: Long, count: Int)
}
