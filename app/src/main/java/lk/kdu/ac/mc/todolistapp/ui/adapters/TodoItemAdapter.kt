package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem

class TodoItemAdapter(
    private val onItemClick: (TodoItem) -> Unit,
    private val onDeleteClick: (TodoItem) -> Unit
) : ListAdapter<TodoItem, TodoItemAdapter.TodoItemViewHolder>(TodoItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TodoItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewTaskDescription)
        private val editButton: ImageButton = itemView.findViewById(R.id.buttonEditTask)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteTask)

        fun bind(item: TodoItem) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            if (item.description.isBlank()) {
                descriptionTextView.visibility = View.GONE
            } else {
                descriptionTextView.visibility = View.VISIBLE
            }

            itemView.setOnClickListener { onItemClick(item) }
            editButton.setOnClickListener { onItemClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }
}

private class TodoItemDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
