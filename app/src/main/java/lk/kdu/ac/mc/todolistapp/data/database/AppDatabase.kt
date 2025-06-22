package lk.kdu.ac.mc.todolistapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import lk.kdu.ac.mc.todolistapp.data.database.dao.TodoListDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity

// This is helps us store and manage all todo lists and tasks
@Database(
    entities = [
        TodoListEntity::class,  // lists
        TodoItemEntity::class   // Individual tasks in those lists
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    // This helps to work with lists and tasks in the database
    abstract fun todoListDao(): TodoListDao

    companion object {
        // Keeps track of database instance
        // @Volatile ==> all threads see the same value
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // This creates or returns database
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_app_database"  // The name of the database file
                )
                // When set to false, the app will crash if schema version changes and no Migration is provided
                // This prevents accidental data loss by not allowing automatic database destruction
                .fallbackToDestructiveMigration(false) // non-deprecated version
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
