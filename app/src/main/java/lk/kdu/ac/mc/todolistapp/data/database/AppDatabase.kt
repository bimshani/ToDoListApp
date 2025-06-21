package lk.kdu.ac.mc.todolistapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lk.kdu.ac.mc.todolistapp.data.database.dao.TodoListDao
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoListEntity
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItemEntity

@Database(
    entities = [
        TodoListEntity::class,
        TodoItemEntity::class
    ],
    version = 2
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
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
