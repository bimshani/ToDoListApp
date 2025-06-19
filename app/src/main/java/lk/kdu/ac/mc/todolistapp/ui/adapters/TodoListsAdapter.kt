package lk.kdu.ac.mc.todolistapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.data.models.TodoList

class TodoListsAdapter(
    private val onItemClick: (TodoList) -> Unit,
    private val onEditClick: (TodoList) -> Unit,
    private val onDeleteClick: (TodoList) -> Unit
) : RecyclerView.Adapter<TodoListsAdapter.ViewHolder>() {

    private var lists = listOf<TodoList>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.textViewTitle)
        val itemCountText: TextView = itemView.findViewById(R.id.textViewItemCount)
        private val editButton: View = itemView.findViewById(R.id.buttonEdit)
        private val deleteButton: View = itemView.findViewById(R.id.buttonDelete)

        init {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(lists[adapterPosition])
                }
            }

            editButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onEditClick(lists[adapterPosition])
                }
            }

            deleteButton.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(lists[adapterPosition])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.todo_list_entry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todoList = lists[position]
        holder.titleText.text = todoList.title
        holder.itemCountText.text = when (todoList.itemCount) {
            0 -> "No items"
            1 -> "1 item"
            else -> "${todoList.itemCount} items"
        }
    }

    override fun getItemCount() = lists.size

    fun submitList(newLists: List<TodoList>) {
        lists = newLists
        notifyDataSetChanged()
    }
}