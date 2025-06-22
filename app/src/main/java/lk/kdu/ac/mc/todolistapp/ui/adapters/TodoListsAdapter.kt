package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.data.models.TodoList

// This adapter shows the list of todo lists in the main screen
// - List title
// - Number of items in the list
// - Edit and delete buttons
class TodoListsAdapter(
    private val onItemClick: (TodoList) -> Unit,     // Opens the list details
    private val onEditClick: (TodoList) -> Unit,     // Opens list edit dialog
    private val onDeleteClick: (TodoList) -> Unit    // Shows delete confirmation
) : RecyclerView.Adapter<TodoListsAdapter.ViewHolder>() {

    private var lists = listOf<TodoList>()

    // Holds and manages all the views for a single list item
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textViewTitle)
        val itemCountText: TextView = itemView.findViewById(R.id.textViewItemCount)
        private val editButton: View = itemView.findViewById(R.id.buttonEdit)
        private val deleteButton: View = itemView.findViewById(R.id.buttonDelete)

        init {
            // Open list when clicking anywhere on the item
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(lists[adapterPosition])
                }
            }

            // Edit list title when edit button clicked
            editButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditClick(lists[adapterPosition])
                }
            }

            // Delete list when delete button clicked
            deleteButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(lists[adapterPosition])
                }
            }
        }
    }

    // Create new views for each list item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_list_entry, parent, false)
        return ViewHolder(view)
    }

    // Update view with list data
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoList = lists[position]
        holder.titleText.text = todoList.title
        // Show text for item count
        holder.itemCountText.text = when (todoList.itemCount) {
            0 -> "No items"
            1 -> "1 item"
            else -> "${todoList.itemCount} items"
        }
    }

    override fun getItemCount() = lists.size

    // Updates the list data and refreshes the view
    fun submitList(newLists: List<TodoList>) {
        lists = newLists
        notifyDataSetChanged()
    }
}