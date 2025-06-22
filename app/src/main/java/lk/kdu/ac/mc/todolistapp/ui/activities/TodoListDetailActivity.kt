package lk.kdu.ac.mc.todolistapp.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.ui.adapters.TodoItemAdapter
import lk.kdu.ac.mc.todolistapp.ui.utils.ItemMoveCallback
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

class TodoListDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TodoItemAdapter
    private lateinit var fabAddTask: ExtendedFloatingActionButton
    private lateinit var listTitleText: TextView
    private var listId: Long = -1
    private var listTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list_detail)

        listId = intent.getLongExtra("LIST_ID", -1)
        listTitle = intent.getStringExtra("LIST_TITLE") ?: ""

        if (listId == -1L) {
            finish()
            return
        }

        setupActionBar()
        setupViewModel()
        setupUI()
        setupObservers()
        setupBackHandler()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = listTitle
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]
    }

    private fun setupUI() {
        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        listTitleText = findViewById(R.id.textViewListTitle)

        listTitleText.text = listTitle

        adapter = TodoItemAdapter(
            onItemClick = { task: TodoItem -> showEditTaskDialog(task) },
            onDeleteClick = { task: TodoItem -> showDeleteTaskDialog(task) },
            onCompletionToggle = { task: TodoItem ->
                val updatedTask = task.copy(isCompleted = !task.isCompleted)
                viewModel.updateTask(updatedTask)
            },
            onItemsReordered = { tasks ->
                viewModel.updateTaskOrder(tasks)
            }
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TodoListDetailActivity)
            adapter = this@TodoListDetailActivity.adapter
        }

        // Set up drag and drop
        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun setupObservers() {
        viewModel.getTasksForList(listId).observe(this) { tasks: List<TodoItem> ->
            adapter.submitList(tasks)
        }
    }

    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun getDialogContext() = android.view.ContextThemeWrapper(this, R.style.AlertDialogTheme)

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)

        AlertDialog.Builder(getDialogContext())
            .setTitle("Add New Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val title = titleEdit.text.toString()
                val description = descriptionEdit.text.toString()
                if (title.isNotBlank()) {
                    val task = TodoItem(
                        listId = listId,
                        title = title,
                        description = description
                    )
                    viewModel.insertTask(task)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditTaskDialog(task: TodoItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)

        titleEdit.setText(task.title)
        descriptionEdit.setText(task.description)

        AlertDialog.Builder(getDialogContext())
            .setTitle("Edit Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val title = titleEdit.text.toString()
                val description = descriptionEdit.text.toString()
                if (title.isNotBlank()) {
                    val updatedTask = task.copy(
                        title = title,
                        description = description
                    )
                    viewModel.updateTask(updatedTask)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteTaskDialog(task: TodoItem) {
        AlertDialog.Builder(getDialogContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
