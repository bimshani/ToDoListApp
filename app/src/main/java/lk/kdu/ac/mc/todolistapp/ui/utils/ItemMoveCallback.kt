package lk.kdu.ac.mc.todolistapp.ui.utils

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

// This class handles drag and drop for reordering items in a list
// It works with any adapter that implements ItemTouchHelperContract
// used by TodoItemAdapter for reordering tasks
class ItemMoveCallback(private val adapter: ItemTouchHelperContract) : ItemTouchHelper.Callback() {

    // Define what kinds of movements are allowed
    // Here only allow up/down dragging
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        return makeMovementFlags(dragFlags, 0)
    }

    // Called when user drags an item to a new position to tell the adapter about the move so it can update its data
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                       target: RecyclerView.ViewHolder): Boolean {
        adapter.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Not implementing swipe functionality
    }

    // When user starts dragging an item, make it slightly transparent to give visual feedback that the item is being moved
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            viewHolder?.itemView?.alpha = 0.9f
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    // When user drops the item, restore its normal appearance and tell the adapter to save the new order
    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = 1.0f
        adapter.onRowClear()
    }

    // Contract that adapters must implement to work with drag and drop
    interface ItemTouchHelperContract {
        fun onRowMoved(fromPosition: Int, toPosition: Int)
        fun onRowClear()
    }
}
