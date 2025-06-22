package lk.kdu.ac.mc.todolistapp.ui.adapters

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
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.ui.utils.ItemMoveCallback
import java.util.Collections

class TodoItemAdapter(
    private val onItemClick: (TodoItem) -> Unit,
    private val onDeleteClick: (TodoItem) -> Unit,
    private val onCompletionToggle: (TodoItem) -> Unit,
    private val onItemsReordered: (List<TodoItem>) -> Unit
) : ListAdapter<TodoItem, TodoItemAdapter.TodoItemViewHolder>(TodoItemDiffCallback()),
    ItemMoveCallback.ItemTouchHelperContract {

    private var items = mutableListOf<TodoItem>()

    override fun submitList(list: List<TodoItem>?) {
        super.submitList(list)
        items = list?.toMutableList() ?: mutableListOf()
    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)

        // Update positions for all affected items
        items.forEachIndexed { index, item ->
            items[index] = item.copy(position = index)
        }
    }

    override fun onRowClear() {
        onItemsReordered(items.toList())
    }

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
        private val checkBoxDone: CheckBox = itemView.findViewById(R.id.checkBoxTaskDone)
        private val editButton: ImageButton = itemView.findViewById(R.id.buttonEditTask)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteTask)

        fun bind(item: TodoItem) {
            titleTextView.text = item.title
            if (item.isCompleted) {
                titleTextView.paintFlags = titleTextView.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                titleTextView.paintFlags = titleTextView.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            descriptionTextView.text = item.description
            if (item.description.isBlank()) {
                descriptionTextView.visibility = View.GONE
            } else {
                descriptionTextView.visibility = View.VISIBLE
            }

            checkBoxDone.isChecked = item.isCompleted
            checkBoxDone.setOnCheckedChangeListener { _, _ ->
                val updatedTask = item.copy(
                    isCompleted = !item.isCompleted,
                    position = item.position  // Preserve the position
                )
                onCompletionToggle(updatedTask)
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
