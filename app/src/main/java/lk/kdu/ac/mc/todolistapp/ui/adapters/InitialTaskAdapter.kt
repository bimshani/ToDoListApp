package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R

// This adapter is used when creating new tasks initially
// It uses EditText fields instead of TextViews
class InitialTaskAdapter : RecyclerView.Adapter<InitialTaskAdapter.TaskViewHolder>() {
    // List to store all the tasks that user is creating
    private val tasks = mutableListOf<TaskItem>()

    // Simple data class to hold task info while creating
    data class TaskItem(
        var title: String = "",
        var description: String = ""
    )

    // ViewHolder that contains the edit fields and delete button for each task
    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleEdit: EditText = view.findViewById(R.id.editTextTaskTitle)
        val descriptionEdit: EditText = view.findViewById(R.id.editTextTaskDescription)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDeleteTask)
    }

    // Creates new view holders for the task list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_initial_task, parent, false)
        return TaskViewHolder(view)
    }

    // Sets up each task item in the list with its data and listeners
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        // Fill in existing task data
        holder.titleEdit.setText(task.title)
        holder.descriptionEdit.setText(task.description)

        // Save title when user finishes editing
        holder.titleEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                task.title = holder.titleEdit.text.toString()
            }
        }

        // Save description when user finishes editing
        holder.descriptionEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                task.description = holder.descriptionEdit.text.toString()
            }
        }

        // Remove the task when delete is clicked
        holder.deleteButton.setOnClickListener {
            removeTask(position)
        }
    }

    override fun getItemCount() = tasks.size

    // Adds a new empty task to the list
    fun addTask() {
        tasks.add(TaskItem())
        notifyItemInserted(tasks.size - 1)
    }

    // Removes a task and updates the list
    private fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

    // Returns a copy of the tasks list for saving
    fun getTasks(): List<TaskItem> = tasks.toList()
}
