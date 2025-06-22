package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.adapters.TodoListsAdapter
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.ui.adapters.InitialTaskAdapter

/**
 * This screen shows all todo lists
 * - See all lists
 * - Search through them by list title
 * - Create new lists
 * - Edit or delete existing lists
 */
class ListCollectionActivity : AppCompatActivity() {
    // UI elements work with
    private lateinit var adapter: TodoListsAdapter           // Shows the list of todo lists
    private lateinit var viewModel: TodoViewModel           // Handles data operations
    private lateinit var recyclerView: RecyclerView         // The scrollable list
    private lateinit var progressBar: ProgressBar           // Loading indicator
    private lateinit var emptyView: TextView                // "No lists" message
    private lateinit var titleText: TextView                // Screen title
    private lateinit var fabAddList: ExtendedFloatingActionButton  // "+ New List" button
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var originalLists: List<TodoList> = listOf()    // Keeps full list for search

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_collection)

        // Setup everything in order
        initializeViews()      // 1. Get all UI elements ready
        setupRecyclerView()    // 2. Set up the scrolling list
        setupObservers()       // 3. Watch for data changes
        startAnimations()      // 4. Start the nice animations
    }

    // Step 1: Find and setup all UI elements
    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewLists)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
        titleText = findViewById(R.id.titleText)
        fabAddList = findViewById(R.id.fabAddList)
        searchView = findViewById(R.id.searchView)

        // Get data handler ready
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        // Setup the search bar
        setupSearch()

        // Make the "New List" button work
        fabAddList.setOnClickListener {
            // shrink animation when clicked
            fabAddList.shrink()
            fabAddList.postDelayed({
                fabAddList.extend()
                showAddListDialog()
            }, 100)
        }
    }

    // Makes the search feature work
    private fun setupSearch() {
        // Make the search text
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText?.apply {
            setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16f)
            setPadding(16, 8, 16, 8)
        }

        // Update results as user types
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterLists(newText)  // Show matching lists
                return true
            }
        })
    }

    // Filter lists based on search text
    private fun filterLists(query: String?) {
        if (query.isNullOrBlank()) {
            // If search is empty, show all lists
            adapter.submitList(originalLists)
            updateEmptyState(originalLists)
            return
        }

        // Show only lists that match the search
        val filteredList = originalLists.filter {
            todoList -> todoList.title.contains(query, ignoreCase = true)
        }
        adapter.submitList(filteredList)
        updateEmptyState(filteredList)
    }

    // Make everything appear with nice animations
    private fun startAnimations() {
        // Start everything hidden
        titleText.apply {
            alpha = 0f
            translationY = -50f
        }
        fabAddList.apply {
            scaleX = 0f
            scaleY = 0f
        }

        // Slide in the title
        titleText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()

        // Pop in the "New List" button
        fabAddList.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setStartDelay(300)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()
    }

    // Show/hide the "No lists" message
    private fun updateEmptyState(lists: List<TodoList>) {
        if (lists.isEmpty()) {
            // If no lists, hide the recycler and show empty message
            recyclerView.visibility = View.GONE
            emptyView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(200).start()
            }
        } else {
            // If have lists, show them and hide empty message
            emptyView.visibility = View.GONE
            recyclerView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(200).start()
            }
        }
    }

    // Setup the scrollable list of todo lists
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoListsAdapter(
            onItemClick = { todoList ->
                // Add a little bounce animation when clicking a list
                recyclerView.findViewHolderForItemId(todoList.id.toLong())?.itemView?.let { view ->
                    view.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(100)
                        .withEndAction {
                            view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .withEndAction {
                                    navigateToListDetail(todoList)
                                }
                                .start()
                        }
                        .start()
                } ?: navigateToListDetail(todoList)
            },
            onEditClick = { todoList -> showEditListDialog(todoList) },
            onDeleteClick = { todoList -> showDeleteConfirmation(todoList) }
        )
        recyclerView.adapter = adapter
    }

    // Open a specific list when clicked
    private fun navigateToListDetail(todoList: TodoList) {
        val intent = Intent(this, TodoListDetailActivity::class.java).apply {
            putExtra("LIST_ID", todoList.id)
            putExtra("LIST_TITLE", todoList.title)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // Watch for changes in our lists
    private fun setupObservers() {
        viewModel.allLists.observe(this) { lists ->
            originalLists = lists
            // If searching, filter the new lists
            if (!searchView.query.isNullOrBlank()) {
                filterLists(searchView.query.toString())
            } else {
                // Otherwise show all lists
                adapter.submitList(lists)
                updateEmptyState(lists)
            }
            progressBar.visibility = View.GONE
        }
    }

    // Get the right theme for our dialogs
    private fun getDialogContext() = android.view.ContextThemeWrapper(this, R.style.AlertDialogTheme)

    // Show dialog to create a new list
    private fun showAddListDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_list_with_tasks, null)
        val listTitleEdit = dialogView.findViewById<EditText>(R.id.editTextListTitle)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewInitialTasks)
        val addTaskButton = dialogView.findViewById<MaterialButton>(R.id.buttonAddTask)

        // Setup the task list in the dialog
        val taskAdapter = InitialTaskAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListCollectionActivity)
            adapter = taskAdapter
        }

        // Let user add multiple tasks
        addTaskButton.setOnClickListener {
            taskAdapter.addTask()
        }

        // Create and show the dialog
        val dialog = AlertDialog.Builder(getDialogContext())
            .setTitle("Create New List")
            .setView(dialogView)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel", null)
            .create()

        // Handle the Create button click
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val title = listTitleEdit.text.toString().trim()
                if (title.isEmpty()) {
                    listTitleEdit.error = "Title is required"
                    return@setOnClickListener
                }

                // Create the list and add any initial tasks
                viewModel.insertList(title) { newListId ->
                    val tasks = taskAdapter.getTasks()
                    tasks.forEach { task ->
                        if (task.title.isNotBlank()) {
                            viewModel.insertTask(
                                TodoItem(
                                    listId = newListId,
                                    title = task.title,
                                    description = task.description
                                )
                            )
                        }
                    }
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // Show dialog to edit a list's title
    private fun showEditListDialog(todoList: TodoList) {
        val editText = EditText(this).apply {
            setText(todoList.title)
            setPadding(32, 32, 32, 32)
            setTextColor(getColor(R.color.text_white))
            setHintTextColor(getColor(R.color.text_secondary))
            backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.text_white))
        }

        AlertDialog.Builder(getDialogContext())
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

    // Show confirmation before deleting a list
    private fun showDeleteConfirmation(todoList: TodoList) {
        AlertDialog.Builder(getDialogContext())
            .setTitle("Delete List")
            .setMessage("Are you sure you want to delete '${todoList.title}'?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteList(todoList)
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Handle back button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Add slide animation when closing the screen
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
