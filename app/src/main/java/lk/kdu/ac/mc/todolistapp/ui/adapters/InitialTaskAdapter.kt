package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R

class InitialTaskAdapter : RecyclerView.Adapter<InitialTaskAdapter.TaskViewHolder>() {
    private val tasks = mutableListOf<TaskItem>()

    data class TaskItem(
        var title: String = "",
        var description: String = ""
    )

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleEdit: EditText = view.findViewById(R.id.editTextTaskTitle)
        val descriptionEdit: EditText = view.findViewById(R.id.editTextTaskDescription)
        val deleteButton: ImageButton = view.findViewById(R.id.buttonDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_initial_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleEdit.setText(task.title)
        holder.descriptionEdit.setText(task.description)

        holder.titleEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                task.title = holder.titleEdit.text.toString()
            }
        }

        holder.descriptionEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                task.description = holder.descriptionEdit.text.toString()
            }
        }

        holder.deleteButton.setOnClickListener {
            removeTask(position)
        }
    }

    override fun getItemCount() = tasks.size

    fun addTask() {
        tasks.add(TaskItem())
        notifyItemInserted(tasks.size - 1)
    }

    private fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, tasks.size)
    }

    fun getTasks(): List<TaskItem> = tasks.toList()
}
