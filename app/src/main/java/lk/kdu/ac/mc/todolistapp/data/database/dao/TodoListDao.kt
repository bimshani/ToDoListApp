package lk.kdu.ac.mc.todolistapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity

@Dao
interface TodoListDao {
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
}
