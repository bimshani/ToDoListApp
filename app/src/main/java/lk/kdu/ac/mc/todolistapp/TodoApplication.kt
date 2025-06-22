package lk.kdu.ac.mc.todolistapp

import android.app.Application
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase

// This is the main application class that initializes app-wide components
// - Provides a single instance of the database that can be accessed throughout the app
// - Uses lazy initialization to create the database only when it's first needed
class TodoApplication : Application() {
    // Single database instance for the whole app
    // Created only when first accessed (lazy initialization)
    // helps with performance and ensures we don't create unnecessary database instances
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
