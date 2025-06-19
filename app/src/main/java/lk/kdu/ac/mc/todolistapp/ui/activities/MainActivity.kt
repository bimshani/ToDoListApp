package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import lk.kdu.ac.mc.todolistapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val goToListsButton = findViewById<Button>(R.id.btnGoToLists)
        goToListsButton.setOnClickListener {
            startActivity(Intent(this, ListsActivity::class.java))
        }
    }
}