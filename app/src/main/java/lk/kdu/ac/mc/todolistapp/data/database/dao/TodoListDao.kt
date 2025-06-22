package lk.kdu.ac.mc.todolistapp.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity

// This is the main database helper file that handles all the todo lists and tasks
@Dao
interface TodoListDao {
    // ===== Todo List Related Stuff =====

    // Gets all the lists we have, newest ones first
    @Query("SELECT * FROM todo_lists ORDER BY createdAt DESC")
    fun getAllLists(): LiveData<List<TodoListEntity>>

    // Adds a new list to the database
    @Insert
    suspend fun insert(todoList: TodoListEntity): Long

    // Updates a list when we change something
    @Update
    suspend fun update(todoList: TodoListEntity)

    // Deletes a list we don't want anymore
    @Delete
    suspend fun delete(todoList: TodoListEntity)

    // Helps find lists by searching their titles
    @Query("SELECT * FROM todo_lists WHERE title LIKE '%' || :query || '%'")
    fun searchLists(query: String): LiveData<List<TodoListEntity>>

    // ===== Task Related Stuff =====

    // Gets all tasks in a specific list, keeps them in order
    @Query("SELECT * FROM todo_items WHERE listId = :listId ORDER BY position ASC, createdAt DESC")
    fun getTasksForList(listId: Long): LiveData<List<TodoItemEntity>>

    // Finds the last position number in a list
    // Helps when adding new tasks in the right order
    @Query("SELECT MAX(position) FROM todo_items WHERE listId = :listId")
    suspend fun getMaxPositionForList(listId: Long): Int?

    // Adds a new task to a list
    @Insert
    suspend fun insertTask(task: TodoItemEntity): Long

    // Updates a task when we change it
    @Update
    suspend fun updateTask(task: TodoItemEntity)

    // Removes a task from the list
    @Delete
    suspend fun deleteTask(task: TodoItemEntity)

    // ===== Counting and Stats Stuff =====

    // Counts how many tasks are still not done
    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 0")
    suspend fun getPendingTasksCount(): Int

    // Counts how many tasks are finished
    @Query("SELECT COUNT(*) FROM todo_items WHERE isCompleted = 1")
    suspend fun getCompletedTasksCount(): Int

    // Counts how many tasks are in a specific list
    @Query("SELECT COUNT(*) FROM todo_items WHERE listId = :listId")
    suspend fun getTaskCountForList(listId: Long): Int

    // Updates how many items are in a list
    @Query("UPDATE todo_lists SET itemCount = :count WHERE id = :listId")
    suspend fun updateListItemCount(listId: Long, count: Int)

    // Finds a specific task by its ID
    @Query("SELECT * FROM todo_items WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TodoItemEntity?
}
