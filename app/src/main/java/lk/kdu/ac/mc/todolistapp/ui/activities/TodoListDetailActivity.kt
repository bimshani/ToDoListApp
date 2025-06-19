package lk.kdu.ac.mc.todolistapp.ui.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

class TodoListDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private var listId: Long = -1
    private var listTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_list_detail)

        listId = intent.getLongExtra("LIST_ID", -1)
        listTitle = intent.getStringExtra("LIST_TITLE") ?: ""

        if (listId == -1L) {
            finish()
            return
        }

        setupActionBar()
        setupViewModel()
        setupUI()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            title = listTitle
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]
    }

    private fun setupUI() {
        // TODO: Setup RecyclerView and FAB for items
    }
}
