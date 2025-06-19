package lk.kdu.ac.mc.todolistapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoItem
import lk.kdu.ac.mc.todolistapp.data.database.entities.TodoList

@Database(
    entities = [TodoList::class, TodoItem::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDatabase? = null

        fun getDatabase(context: Context): TodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java,
                    "todo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}