package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.R

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var welcomeAnimation: LottieAnimationView
    private lateinit var welcomeText: TextView
    private lateinit var developerText: TextView
    private lateinit var goToListsButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        initializeViews()
        setupClickListeners()
        startAnimations()
    }

    private fun initializeViews() {
        welcomeAnimation = findViewById(R.id.welcomeAnimation)
        welcomeText = findViewById(R.id.textViewWelcome)
        developerText = findViewById(R.id.textViewDeveloper)
        goToListsButton = findViewById(R.id.buttonGoToLists)

        // Set name and student ID
        welcomeText.text = "Welcome to Bimshani Upethra's TO DO List!"
        developerText.text = "This App was developed by NTPGB Upethra - D/BCS/23/0021"

        // Set initial states for animations
        welcomeText.alpha = 0f
        developerText.alpha = 0f
        goToListsButton.scaleX = 0f
        goToListsButton.scaleY = 0f
    }

    private fun startAnimations() {
        welcomeText.animate()
            .alpha(1f)
            .setDuration(500)
            .start()

        developerText.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(200)
            .start()

        goToListsButton.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(400)
            .start()
    }

    private fun setupClickListeners() {
        goToListsButton.setOnClickListener {
            val intent = Intent(this, ListCollectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}