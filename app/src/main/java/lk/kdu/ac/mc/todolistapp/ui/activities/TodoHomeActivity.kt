package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

/**
 * This is dashboard screen. it shows:
 * - How many tasks are waiting to be done
 * - How many tasks have finished
 * - Buttons to view or create lists
 */
class TodoHomeActivity : AppCompatActivity() {
    // UI elements to show task counts
    private lateinit var viewModel: TodoViewModel         // Handles data
    private lateinit var pendingTasksCount: TextView      // Shows unfinished tasks
    private lateinit var completedTasksCount: TextView    // Shows finished tasks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo_home)

        setupViews()           // 1. Get UI elements ready
        setupButtons()         // 2. Make buttons work
    }

    // 1. Find UI elements
    private fun setupViews() {
        pendingTasksCount = findViewById(R.id.pendingTasksCount)
        completedTasksCount = findViewById(R.id.completedTasksCount)
    }

    // 2. Make our buttons do things
    private fun setupButtons() {
        // "View Lists" button to shows all lists
        findViewById<MaterialButton>(R.id.btnViewTasks).setOnClickListener {
            startActivity(Intent(this, ListCollectionActivity::class.java))
        }

        // "Create List" button by opens the create dialog right away
        findViewById<MaterialButton>(R.id.btnCreateList).setOnClickListener {
            val intent = Intent(this, ListCollectionActivity::class.java)
            intent.putExtra("SHOW_CREATE_DIALOG", true)
            startActivity(intent)
        }
    }
}
