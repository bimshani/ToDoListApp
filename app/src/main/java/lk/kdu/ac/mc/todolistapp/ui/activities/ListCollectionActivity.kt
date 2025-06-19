package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.adapters.TodoListsAdapter
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

class ListCollectionActivity : AppCompatActivity() {
    private lateinit var adapter: TodoListsAdapter
    private lateinit var viewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_collection)

        // Set up the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Lists"

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        setupRecyclerView()
        setupFab()

        // Check if we should show the create dialog
        if (intent.getBooleanExtra("SHOW_CREATE_DIALOG", false)) {
            showAddListDialog()
        }

        // Observe lists using the allLists property
        viewModel.allLists.observe(this) { lists ->
            adapter.submitList(lists)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewLists)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoListsAdapter(
            onItemClick = { todoList ->
                startActivity(Intent(this, TodoListDetailActivity::class.java).apply {
                    putExtra("LIST_ID", todoList.id)
                    putExtra("LIST_TITLE", todoList.title)
                })
            },
            onEditClick = { todoList ->
                showEditListDialog(todoList)
            },
            onDeleteClick = { todoList ->
                showDeleteConfirmation(todoList)
            }
        )
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        findViewById<FloatingActionButton>(R.id.fabAddList).setOnClickListener {
            showAddListDialog()
        }
    }

    private fun showAddListDialog() {
        val editText = EditText(this).apply {
            hint = "Enter list title"
            setPadding(32, 32, 32, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Create New List")
            .setView(editText)
            .setPositiveButton("Create") { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    viewModel.insertList(title)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditListDialog(todoList: TodoList) {
        val editText = EditText(this).apply {
            setText(todoList.title)
            setPadding(32, 32, 32, 32)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit List")
            .setView(editText)
            .setPositiveButton("Update") { _, _ ->
                val title = editText.text.toString().trim()
                if (title.isNotEmpty()) {
                    viewModel.updateList(todoList.copy(title = title))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(todoList: TodoList) {
        AlertDialog.Builder(this)
            .setTitle("Delete List")
            .setMessage("Are you sure you want to delete '${todoList.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteList(todoList)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
