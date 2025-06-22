package lk.kdu.ac.mc.todolistapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.data.repository.TodoListRepository

// This ViewModel handles all the business logic for todo lists and tasks
// It connects the UI with the data layer (repository)
// Features:
// - Managing todo lists (create, update, delete, search)
// - Managing tasks within lists
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoListRepository
    val allLists: LiveData<List<TodoList>>  // Live updates of all todo lists

    init {
        // Set up database access and repository
        val dao = AppDatabase.getDatabase(application).todoListDao()
        repository = TodoListRepository(dao)
        allLists = repository.getAllLists()
    }

    // List Management Functions

    // Creates a new list and returns its ID through the callback
    fun insertList(title: String, onListCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val newListId = repository.insertList(title)
            onListCreated(newListId)
        }
    }

    // Updates existing list details
    fun updateList(todoList: TodoList) {
        viewModelScope.launch {
            repository.updateList(todoList)
        }
    }

    // Deletes a list and all its tasks
    fun deleteList(todoList: TodoList) {
        viewModelScope.launch {
            repository.deleteList(todoList)
        }
    }

    // Searches lists by title
    fun searchLists(query: String): LiveData<List<TodoList>> {
        return repository.searchLists(query)
    }

    // Task Management Functions

    // Gets all tasks for a specific list
    fun getTasksForList(listId: Long): LiveData<List<TodoItem>> {
        return repository.getTasksForList(listId)
    }

    // Adds a new task to a list
    fun insertTask(task: TodoItem) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    // Updates task details while preserving its position in the list
    fun updateTask(task: TodoItem) {
        viewModelScope.launch {
            // Keep the existing position if task is being updated
            val existingTask = repository.getTaskById(task.id)
            val updatedTask = task.copy(position = existingTask?.position ?: task.position)
            repository.updateTask(updatedTask)
        }
    }

    // Removes a task from its list
    fun deleteTask(task: TodoItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    // Saves the new order after drag-drop reordering
    // Only updates tasks whose position actually changed
    fun updateTaskOrder(tasks: List<TodoItem>) {
        viewModelScope.launch {
            tasks.forEachIndexed { index, task ->
                if (task.position != index) {
                    repository.updateTask(task.copy(position = index))
                }
            }
        }
    }
}
