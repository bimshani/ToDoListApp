package lk.kdu.ac.mc.todolistapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.data.repository.TodoListRepository

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoListRepository
    val allLists: LiveData<List<TodoList>>

    // Task statistics
    private val _taskStats = MutableLiveData<Pair<Int, Int>>()
    val taskStats: LiveData<Pair<Int, Int>> = _taskStats

    init {
        val dao = AppDatabase.getDatabase(application).todoListDao()
        repository = TodoListRepository(dao)
        allLists = repository.getAllLists()
        updateTaskStats()
    }

    fun insertList(title: String) {
        viewModelScope.launch {
            repository.insertList(title)
            updateTaskStats()
        }
    }

    fun updateList(todoList: TodoList) {
        viewModelScope.launch {
            repository.updateList(todoList)
            updateTaskStats()
        }
    }

    fun deleteList(todoList: TodoList) {
        viewModelScope.launch {
            repository.deleteList(todoList)
            updateTaskStats()
        }
    }

    fun searchLists(query: String): LiveData<List<TodoList>> {
        return repository.searchLists(query)
    }

    fun getTasksForList(listId: Long): LiveData<List<TodoItem>> {
        return repository.getTasksForList(listId)
    }

    fun insertTask(task: TodoItem) {
        viewModelScope.launch {
            repository.insertTask(task)
            updateTaskStats()
        }
    }

    fun updateTask(task: TodoItem) {
        viewModelScope.launch {
            repository.updateTask(task)
            updateTaskStats()
        }
    }

    fun deleteTask(task: TodoItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
            updateTaskStats()
        }
    }

    private fun updateTaskStats() {
        viewModelScope.launch {
            val pendingTasks = repository.getPendingTasksCount()
            val completedTasks = repository.getCompletedTasksCount()
            _taskStats.value = Pair(pendingTasks, completedTasks)
        }
    }
}
