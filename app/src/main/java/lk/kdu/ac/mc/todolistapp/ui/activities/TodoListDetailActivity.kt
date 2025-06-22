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

/**
 * This screen shows all tasks in a to do list
 * - See all tasks in the list
 * - Add new tasks
 * - Edit existing tasks
 * - Mark tasks as done/undone
 * - Reorder tasks by dragging them
 */
class TodoListDetailActivity : AppCompatActivity() {
    // UI elements we'll work with
    private lateinit var viewModel: TodoViewModel          // Handles data operations
    private lateinit var recyclerView: RecyclerView       // Shows the list of tasks
    private lateinit var adapter: TodoItemAdapter         // Manages task items in the list
    private lateinit var fabAddTask: ExtendedFloatingActionButton  // new task button
    private lateinit var listTitleText: TextView          // Shows the list's title

    // Info about which list is viewing
    private var listId: Long = -1
    private var listTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list_detail)

        // Get the list info that was passed to the screen
        listId = intent.getLongExtra("LIST_ID", -1)
        listTitle = intent.getStringExtra("LIST_TITLE") ?: ""

        // If no valid list ID, close the screen
        if (listId == -1L) {
            finish()
            return
        }

        // Setup everything in order
        setupActionBar()      // 1. Set up the top bar
        setupViewModel()      // 2. Get our data handler ready
        setupUI()            // 3. Set up all UI elements
        setupObservers()     // 4. Watch for task updates
        setupBackHandler()   // 5. Handle back button
    }

    // 1. Set up the top bar with list title and back button
    private fun setupActionBar() {
        supportActionBar?.apply {
            title = listTitle
            setDisplayHomeAsUpEnabled(true)  // Show back button
        }
    }

    // 2. Get data handler ready
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]
    }

    // 3. Set up all the screen's UI elements
    private fun setupUI() {
        // Find our UI elements
        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        listTitleText = findViewById(R.id.textViewListTitle)
        listTitleText.text = listTitle

        // Set up the task list adapter
        setupAdapter()

        // Show "Add Task" dialog when the button is clicked
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    // Setup adapter with completion toggle handling
    private fun setupAdapter() {
        adapter = TodoItemAdapter(
            onItemClick = { task -> showEditTaskDialog(task) },        // Edit when clicked
            onDeleteClick = { task -> showDeleteTaskDialog(task) },    // Delete when trash clicked
            onCompletionToggle = { task ->                            // Toggle done or not done
                // Update task completion state in database
                viewModel.updateTask(task.copy(isCompleted = !task.isCompleted))
            },
            onItemsReordered = { tasks ->                            // Save new task order
                viewModel.updateTaskOrder(tasks)
            }
        )

        // Enable drag-and-drop reordering
        val callback = ItemMoveCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerView)

        // Set up the scrolling task list
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TodoListDetailActivity)
            adapter = this@TodoListDetailActivity.adapter
        }

        // Observe tasks and update UI
        viewModel.getTasksForList(listId).observe(this) { tasks ->
            adapter.submitList(tasks)  // Update the list whenever tasks change
        }
    }

    // Step 4: Watch for changes in our tasks
    private fun setupObservers() {
        viewModel.getTasksForList(listId).observe(this) { tasks ->
            adapter.submitList(tasks)  // Update the list whenever tasks change
        }
    }

    // Step 5: Handle back button presses
    private fun setupBackHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()  // Just close the screen
            }
        })
    }

    // Get the right theme for our dialogs
    private fun getDialogContext() = android.view.ContextThemeWrapper(this, R.style.AlertDialogTheme)

    // Show dialog to add a new task
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

    // Show dialog to edit an existing task
    private fun showEditTaskDialog(task: TodoItem) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.editTextTaskTitle)
        val descriptionEdit = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)

        // Fill in existing task details
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

    // Show confirmation before deleting a task
    private fun showDeleteTaskDialog(task: TodoItem) {
        AlertDialog.Builder(getDialogContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Handle clicks on the action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
