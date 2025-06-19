package lk.kdu.ac.mc.todolistapp.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.data.repository.TodoListRepository

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoListRepository
    val allLists: LiveData<List<TodoList>>

    // Task statistics
    private val _taskStats = MutableLiveData<Pair<Int, Int>>() // Pending, Completed
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

    private fun updateTaskStats() {
        viewModelScope.launch {
            // For now, we'll use itemCount as a placeholder for pending tasks
            // This should be updated when we implement actual task tracking
            _taskStats.value = Pair(0, 0)
        }
    }
}
