package lk.kdu.ac.mc.todolistapp.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseBackup {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun backupToFirebase(context: Context) {
        // Authenticate anonymously for simplicity
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                performBackup(context)
            } else {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performBackup(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val backupRef = database.reference.child("backups").child(userId)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val todoDao = AppDatabase.getDatabase(context).todoListDao()
                val allItems = mutableListOf<TodoItemEntity>()

                // Get all lists and items
                val lists = todoDao.getAllLists().value ?: emptyList()
                lists.forEach { list: TodoListEntity ->
                    val items = todoDao.getTasksForList(list.id).value ?: emptyList()
                    allItems.addAll(items)
                }

                // Create backup data
                val backupData = mapOf(
                    "lists" to lists,
                    "items" to allItems,
                    "timestamp" to System.currentTimeMillis()
                )

                // Upload to Firebase
                backupRef.setValue(backupData).addOnCompleteListener { task ->
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.Main) {
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Backup successful", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Backup failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Backup error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun restoreFromFirebase(context: Context) {
        // Authenticate anonymously for simplicity
        auth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                performRestore(context)
            } else {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun performRestore(context: Context) {
        val userId = auth.currentUser?.uid ?: return
        val backupRef = database.reference.child("backups").child(userId)

        backupRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val todoDao = AppDatabase.getDatabase(context).todoListDao()

                            // Parse backup data
                            val listsData = snapshot.child("lists").children
                            val itemsData = snapshot.child("items").children

                            // Restore lists
                            listsData.forEach { listSnapshot ->
                                val title = listSnapshot.child("title").getValue(String::class.java) ?: ""
                                val createdAt = listSnapshot.child("createdAt").getValue(Long::class.java) ?: System.currentTimeMillis()

                                val todoList = TodoListEntity(title = title, createdAt = createdAt)
                                todoDao.insert(todoList)
                            }

                            // Restore items
                            itemsData.forEach { itemSnapshot ->
                                val listId = itemSnapshot.child("listId").getValue(Long::class.java) ?: 0L
                                val title = itemSnapshot.child("title").getValue(String::class.java) ?: ""
                                val description = itemSnapshot.child("description").getValue(String::class.java) ?: ""
                                val isCompleted = itemSnapshot.child("isCompleted").getValue(Boolean::class.java) ?: false
                                val position = itemSnapshot.child("position").getValue(Int::class.java) ?: 0
                                val createdAt = itemSnapshot.child("createdAt").getValue(Long::class.java) ?: System.currentTimeMillis()

                                val todoItem = TodoItemEntity(
                                    listId = listId,
                                    title = title,
                                    description = description,
                                    isCompleted = isCompleted,
                                    position = position,
                                    createdAt = createdAt
                                )
                                todoDao.insertTask(todoItem)
                            }

                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Restore successful", Toast.LENGTH_SHORT).show()
                            }

                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Restore error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "No backup found", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Failed to retrieve backup", Toast.LENGTH_SHORT).show()
            }
        }
    }
}