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
import lk.kdu.ac.mc.todolistapp.datasource.database.entities.TodoEntry

class TodoEntryAdapter(
    private val onItemClick: (TodoEntry) -> Unit,
    private val onDeleteClick: (TodoEntry) -> Unit,
    private val onEditClick: (TodoEntry) -> Unit
) : ListAdapter<TodoEntry, TodoEntryAdapter.ViewHolder>(DiffCallback()) {

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
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewDescription)
        private val editButton: ImageButton = itemView.findViewById(R.id.buttonEditItem)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteItem)

        fun bind(todoEntry: TodoEntry) {
            checkBox.isChecked = todoEntry.isCompleted
            descriptionTextView.text = todoEntry.description

            // Strike through completed items
            if (todoEntry.isCompleted) {
                descriptionTextView.paintFlags = descriptionTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                descriptionTextView.paintFlags = descriptionTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkBox.setOnClickListener { onItemClick(todoEntry) }
            editButton.setOnClickListener { onEditClick(todoEntry) }
            deleteButton.setOnClickListener { onDeleteClick(todoEntry) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TodoEntry>() {
        override fun areItemsTheSame(oldItem: TodoEntry, newItem: TodoEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TodoEntry, newItem: TodoEntry): Boolean {
            return oldItem == newItem
        }
    }
}