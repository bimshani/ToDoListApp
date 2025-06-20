package lk.kdu.ac.mc.todolistapp.ui.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import lk.kdu.ac.mc.todolistapp.R
import lk.kdu.ac.mc.todolistapp.ui.viewmodels.TodoViewModel

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var viewModel: TodoViewModel
    private lateinit var pendingTasksCount: TextView
    private lateinit var completedTasksCount: TextView
    private lateinit var welcomeAnimation: LottieAnimationView
    private lateinit var statsCard: MaterialCardView
    private lateinit var fabAddTask: ExtendedFloatingActionButton
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        initializeViews()
        setupViewModel()
        setupClickListeners()
        startAnimations()
    }

    private fun initializeViews() {
        welcomeAnimation = findViewById(R.id.welcomeAnimation)
        statsCard = findViewById(R.id.statsCard)
        fabAddTask = findViewById(R.id.fab_add_task)
        welcomeText = findViewById(R.id.textViewWelcome)
        pendingTasksCount = findViewById(R.id.pendingTasksCount)
        completedTasksCount = findViewById(R.id.completedTasksCount)

        // Set initial states
        statsCard.alpha = 0f
        welcomeText.alpha = 0f
        fabAddTask.scaleX = 0f
        fabAddTask.scaleY = 0f
    }

    private fun startAnimations() {
        welcomeText.animate()
            .alpha(1f)
            .setDuration(500)
            .start()

        statsCard.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(200)
            .start()

        fabAddTask.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay(400)
            .start()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        viewModel.taskStats.observe(this) { stats ->
            animateNumberChange(pendingTasksCount, stats.first)
            animateNumberChange(completedTasksCount, stats.second)
        }
    }

    private fun setupClickListeners() {
        fabAddTask.setOnClickListener {
            ObjectAnimator.ofFloat(it, "scaleX", 1f, 0.9f, 1f).apply {
                duration = 200
                start()
            }
            ObjectAnimator.ofFloat(it, "scaleY", 1f, 0.9f, 1f).apply {
                duration = 200
                start()
            }
            val intent = Intent(this, ListCollectionActivity::class.java).apply {
                putExtra("openAddTaskDialog", true)
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun animateNumberChange(textView: TextView, newValue: Int) {
        textView.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                textView.text = newValue.toString()
                textView.animate()
                    .alpha(1f)
                    .setDuration(150)
                    .start()
            }
            .start()
    }
}