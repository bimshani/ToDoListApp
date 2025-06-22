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
import com.airbnb.lottie.LottieAnimationView
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.adapters.TodoListsAdapter
import lk.kdu.ac.mc.todolistapp.data.models.TodoList
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.ui.adapters.InitialTaskAdapter

class ListCollectionActivity : AppCompatActivity() {
    private lateinit var adapter: TodoListsAdapter
    private lateinit var viewModel: TodoViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyView: TextView
    private lateinit var titleText: TextView
    private lateinit var fabAddList: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_collection)

        initializeViews()
        setupRecyclerView()
        setupObservers()
        startAnimations()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewLists)
        progressBar = findViewById(R.id.progressBar)
        emptyView = findViewById(R.id.emptyView)
        titleText = findViewById(R.id.titleText)
        fabAddList = findViewById(R.id.fabAddList)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        // Set click listener for FAB
        fabAddList.setOnClickListener {
            // Add shrink and extend animation to FAB when clicked
            fabAddList.shrink()
            fabAddList.postDelayed({
                fabAddList.extend()
                showAddListDialog()
            }, 100)
        }
    }

    private fun startAnimations() {
        // Reset initial states
        titleText.alpha = 0f
        titleText.translationY = -50f
        fabAddList.scaleX = 0f
        fabAddList.scaleY = 0f

        // Animate title
        titleText.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .start()

        // Animate FAB
        fabAddList.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(200)
            .setStartDelay(300)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()
    }

    private fun updateEmptyState(lists: List<TodoList>) {
        if (lists.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        } else {
            emptyView.visibility = View.GONE
            recyclerView.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(200)
                    .start()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TodoListsAdapter(
            onItemClick = { todoList ->
                // Animate the clicked item
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

    private fun navigateToListDetail(todoList: TodoList) {
        val intent = Intent(this, TodoListDetailActivity::class.java).apply {
            putExtra("LIST_ID", todoList.id)
            putExtra("LIST_TITLE", todoList.title)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun setupObservers() {
        // Observe lists using the allLists property
        viewModel.allLists.observe(this) { lists ->
            adapter.submitList(lists)
            updateEmptyState(lists)
        }
    }

    private fun getDialogContext() = android.view.ContextThemeWrapper(this, R.style.AlertDialogTheme)

    private fun showAddListDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_create_list_with_tasks, null)
        val listTitleEdit = dialogView.findViewById<EditText>(R.id.editTextListTitle)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.recyclerViewInitialTasks)
        val addTaskButton = dialogView.findViewById<MaterialButton>(R.id.buttonAddTask)

        val taskAdapter = InitialTaskAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListCollectionActivity)
            adapter = taskAdapter
        }

        addTaskButton.setOnClickListener {
            taskAdapter.addTask()
        }

        val dialog = AlertDialog.Builder(getDialogContext())
            .setTitle("Create New List")
            .setView(dialogView)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val title = listTitleEdit.text.toString().trim()
                if (title.isEmpty()) {
                    listTitleEdit.error = "Title is required"
                    return@setOnClickListener
                }

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
