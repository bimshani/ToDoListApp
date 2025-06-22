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
import lk.kdu.ac.mc.todolistapp.data.models.TodoItem
import lk.kdu.ac.mc.todolistapp.ui.utils.ItemMoveCallback
import java.util.Collections

// This adapter handles the display and interaction of todo tasks in a list
// - Showing task title and description
// - Marking tasks as complete/incomplete
// - Editing and deleting tasks
// - Drag and drop reordering
class TodoItemAdapter(
    private val onItemClick: (TodoItem) -> Unit,        // Called when user wants to edit a task
    private val onDeleteClick: (TodoItem) -> Unit,      // Called when user deletes a task
    private val onCompletionToggle: (TodoItem) -> Unit, // Called when task is marked done/undone
    private val onItemsReordered: (List<TodoItem>) -> Unit // Called after drag-drop reordering
) : ListAdapter<TodoItem, TodoItemAdapter.TodoItemViewHolder>(TodoItemDiffCallback()),
    ItemMoveCallback.ItemTouchHelperContract {

    // Keep a separate list for drag-drop operations
    private var items = mutableListOf<TodoItem>()

    // Update both the ListAdapter's list and our working copy
    override fun submitList(list: List<TodoItem>?) {
        super.submitList(list)
        items = list?.toMutableList() ?: mutableListOf()
    }

    // Handles the actual moving of items during drag & drop
    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            // Moving item down in the list
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            // Moving item up in the list
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)

        // Make sure each item's position matches its place in the list
        items.forEachIndexed { index, item ->
            items[index] = item.copy(position = index)
        }
    }

    // Called when user finishes drag & drop
    override fun onRowClear() {
        onItemsReordered(items.toList())
    }

    // Create new views for each task item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TodoItemViewHolder(view)
    }

    // Update view with task data
    override fun onBindViewHolder(holder: TodoItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Holds and manages all the views for a single task item
    inner class TodoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTaskTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewTaskDescription)
        private val checkBoxDone: CheckBox = itemView.findViewById(R.id.checkBoxTaskDone)
        private val editButton: ImageButton = itemView.findViewById(R.id.buttonEditTask)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.buttonDeleteTask)

        // Sets up all the views with task data and click listeners
        fun bind(item: TodoItem) {
            // Set text content
            titleTextView.text = item.title
            descriptionTextView.text = item.description

            // Handle completion state
            checkBoxDone.isChecked = item.isCompleted
            updateStrikeThrough(item.isCompleted)

            // Show/hide description
            descriptionTextView.visibility = if (item.description.isBlank()) {
                View.GONE
            } else {
                View.VISIBLE
            }

            // Set up checkbox click listener
            checkBoxDone.setOnClickListener {
                onCompletionToggle(item)
                updateStrikeThrough(checkBoxDone.isChecked)
            }

            // Other click listeners
            itemView.setOnClickListener { onItemClick(item) }
            editButton.setOnClickListener { onItemClick(item) }
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }

        private fun updateStrikeThrough(isCompleted: Boolean) {
            val flag = if (isCompleted) Paint.STRIKE_THRU_TEXT_FLAG else 0
            titleTextView.paintFlags = flag
            if (descriptionTextView.visibility == View.VISIBLE) {
                descriptionTextView.paintFlags = flag
            }
        }
    }
}

// Helps RecyclerView efficiently update only changed items
private class TodoItemDiffCallback : DiffUtil.ItemCallback<TodoItem>() {
    // Check if items represent the same task (using ID)
    override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem.id == newItem.id
    }

    // Check if all content in the items is exactly the same
    override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
        return oldItem == newItem
    }
}
