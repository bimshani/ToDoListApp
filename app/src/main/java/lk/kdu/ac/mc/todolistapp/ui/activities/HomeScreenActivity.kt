package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import lk.kdu.ac.mc.todolistapp.R

class HomeScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val goToListsButton = findViewById<Button>(R.id.btnGoToLists)
        goToListsButton.setOnClickListener {
            startActivity(Intent(this, ListCollectionActivity::class.java))
        }
    }
}