package lk.kdu.ac.mc.todolistapp

import android.app.Application
import lk.kdu.ac.mc.todolistapp.data.database.AppDatabase

class TodoApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
