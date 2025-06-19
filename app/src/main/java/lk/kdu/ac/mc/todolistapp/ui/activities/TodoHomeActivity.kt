package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

class TodoHomeActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private lateinit var pendingTasksCount: TextView
    private lateinit var completedTasksCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_home)

        // Initialize views
        pendingTasksCount = findViewById(R.id.pendingTasksCount)
        completedTasksCount = findViewById(R.id.completedTasksCount)

        // Setup buttons
        findViewById<MaterialButton>(R.id.btnViewTasks).setOnClickListener {
            startActivity(Intent(this, ListCollectionActivity::class.java))
        }

        findViewById<MaterialButton>(R.id.btnCreateList).setOnClickListener {
            val intent = Intent(this, ListCollectionActivity::class.java)
            intent.putExtra("SHOW_CREATE_DIALOG", true)
            startActivity(intent)
        }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        // Observe task statistics
        viewModel.taskStats.observe(this) { (pending, completed) ->
            pendingTasksCount.text = pending.toString()
            completedTasksCount.text = completed.toString()
        }
    }
}
