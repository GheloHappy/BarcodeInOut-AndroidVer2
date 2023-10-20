package LocalDb

import MssqlCon.PublicVars
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "barcodesys.db"
        private const val DATABASE_VERSION = 4

        private const val TABLE_NAME = "user"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_DEPARTMENT = "department"
        private const val COLUMN_WAREHOUSE = "warehouse"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_USERNAME TEXT, $COLUMN_PASSWORD TEXT, " +
                    "$COLUMN_NAME TEXT, $COLUMN_DEPARTMENT TEXT, $COLUMN_WAREHOUSE TEXT)"
        try {
            db?.execSQL(createTableQuery)
        } catch (e: SQLException) {
            e.printStackTrace();
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        try {
            db?.execSQL(dropTableQuery)
            onCreate(db)
        } catch (e: SQLException) {
            e.printStackTrace();
        }
    }

    fun insertUser(user: User) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_USERNAME, user.username)
                put(COLUMN_PASSWORD, user.password)
                put(COLUMN_NAME, user.name)
                put(COLUMN_DEPARTMENT, user.department)
                put(COLUMN_WAREHOUSE, user.warehouse)
            }
            db.insert(TABLE_NAME, null, values)
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun clearUser(warehouse: String) {
        val db = writableDatabase
        try {
            db.delete(TABLE_NAME, warehouse, null)
        } catch (e: SQLException){
            e.printStackTrace()
        } finally{
            db.close()
        }
    }

//    fun userNotEmpty(): Boolean {
//        val db = readableDatabase
//        val query = "SELECT COUNT(*) FROM $TABLE_NAME"
//        val cursor = db.rawQuery(query, null)
//
//        cursor.use {
//            return try {
//                if (cursor.moveToFirst()) {
//                    val count = cursor.getInt(0)
//                    count > 0
//                } else {
//                    false
//                }
//            } finally {
//                cursor.close()
//            }
//        }
//    }

    fun localLoginUser(username: String, password: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE username = ? AND password = ?"
        val cursor = db.rawQuery(query, arrayOf(username, password))

        cursor.use {
            if (it.moveToFirst()) {
                val count = it.getInt(0)
                return count > 0
            }
        }
        return false
    }
}