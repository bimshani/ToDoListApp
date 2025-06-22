package lk.kdu.ac.mc.todolistapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.button.MaterialButton
import lk.kdu.ac.mc.todolistapp.R

// This is the first screen users see when they open the app
// It shows a welcome message, and a button to start using the app
class HomeScreenActivity : AppCompatActivity() {
    // These are the UI elements we'll work with
    private lateinit var welcomeAnimation: LottieAnimationView  // animation at the top
    private lateinit var welcomeText: TextView                  // Welcome message
    private lateinit var developerText: TextView               // my info
    private lateinit var goToListsButton: MaterialButton       // Button to start the app ==> go to lists screen

    // This runs when the screen is first created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Setup everything in order
        initializeViews()        // 1. Get all UI elements ready
        setupClickListeners()    // 2. Make buttons work
        startAnimations()        // 3. Start the cool fade-in effects
    }

    // 1. Find all UI elements and set them up
    private fun initializeViews() {
        // Find all views from the layout
        welcomeAnimation = findViewById(R.id.welcomeAnimation)
        welcomeText = findViewById(R.id.textViewWelcome)
        developerText = findViewById(R.id.textViewDeveloper)
        goToListsButton = findViewById(R.id.buttonGoToLists)

        // Set the text
        welcomeText.text = "Welcome to Bimshani Upethra's TO DO List!"
        developerText.text = "This App was developed by NTPGB Upethra - D/BCS/23/0021"

        // Make everything invisible at first (for the animation)
        welcomeText.alpha = 0f      // Fully transparent
        developerText.alpha = 0f    // Fully transparent
        goToListsButton.scaleX = 0f // Tiny size and will grow
        goToListsButton.scaleY = 0f
    }

    // 2. Setup what happens when user clicks the button
    private fun setupClickListeners() {
        goToListsButton.setOnClickListener {
            // When button is clicked, go to the lists screen with fade effect
            val intent = Intent(this, ListCollectionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    // 3. Make everything appear with animations
    private fun startAnimations() {
        // Fade in the welcome text first
        welcomeText.animate()
            .alpha(1f)           // Make it fully visible
            .setDuration(500)    // Take half a second
            .start()

        // Fade in the developer info next
        developerText.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(200)  // Wait a bit before starting
            .start()

        // make the button grow into view
        goToListsButton.animate()
            .scaleX(1f)          // Full size
            .scaleY(1f)
            .setDuration(300)
            .setStartDelay(400)  // Wait before showing
            .start()
    }
}