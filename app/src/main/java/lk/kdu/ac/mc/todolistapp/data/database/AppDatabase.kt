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

@Database(
    entities = [
        TodoListEntity::class,
        TodoItemEntity::class
    ],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoListDao(): TodoListDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_app_database"
                )
                .fallbackToDestructiveMigration() // This will recreate tables if migration fails
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
