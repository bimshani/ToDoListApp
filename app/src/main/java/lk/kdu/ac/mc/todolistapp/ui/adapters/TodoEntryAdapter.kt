package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity

class TodoEntryAdapter(
    private val onItemClick: (TodoItemEntity) -> Unit,
    private val onDeleteClick: (TodoItemEntity) -> Unit,
    private val onEditClick: (TodoItemEntity) -> Unit
) : ListAdapter<TodoItemEntity, TodoEntryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.entry_todo, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxItem)
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        private val editButton: ImageButton = itemView.findViewById(R.id.buttonEditItem)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteItem)

        fun bind(todoItem: TodoItemEntity) {
            checkBox.isChecked = todoItem.isCompleted
            titleTextView.text = todoItem.title
            descriptionTextView.text = todoItem.description
            descriptionTextView.visibility = if (todoItem.description.isBlank()) View.GONE else View.VISIBLE

            // Strike through completed items
            val textFlags = if (todoItem.isCompleted) {
                titleTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            titleTextView.paintFlags = textFlags
            descriptionTextView.paintFlags = textFlags

            checkBox.setOnClickListener { onItemClick(todoItem) }
            editButton.setOnClickListener { onEditClick(todoItem) }
            deleteButton.setOnClickListener { onDeleteClick(todoItem) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TodoItemEntity>() {
        override fun areItemsTheSame(oldItem: TodoItemEntity, newItem: TodoItemEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoItemEntity, newItem: TodoItemEntity): Boolean {
            return oldItem == newItem
        }
    }
}