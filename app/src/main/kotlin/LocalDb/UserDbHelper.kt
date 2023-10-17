package LocalDb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "barcodesys.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DEPARTMENT = "department"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_USERNAME TEXT, $COLUMN_PASSWORD TEXT, " +
                "$COLUMN_NAME TEXT, $COLUMN_DEPARTMENT TEXT)"
        db?.execSQL(createTableQuery);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertUser(user: User){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, user.username)
            put(COLUMN_PASSWORD, user.password)
            put(COLUMN_NAME, user.name)
            put(COLUMN_DEPARTMENT, user.department)
        }
        db.insert(TABLE_NAME, null, values)
        db.close();
    }

    fun getUser(username: String): Boolean {
       val db = readableDatabase
       val query = "SELECT COUNT(*)  FROM $TABLE_NAME WHERE username = ?"
        val cursor = db.rawQuery(query, arrayOf(username))

        cursor.use {
            if(it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }
        return false
    }
}