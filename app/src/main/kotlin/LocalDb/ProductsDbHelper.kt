package LocalDb

import MssqlCon.PublicVars
import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ProductsDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private val DATABASE_NAME = PublicVars.DATABASE_NAME
        private val DATABASE_VERSION = PublicVars.DATABASE_VERSION

        private const val TABLE_NAME = "products"
        private const val COLUMN_ID = "id"
        private const val COLUMN_BARCODE = "barcode"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_SOLOMON_ID = "solomonID"
        private const val COLUMN_UOM = "uom"
        private const val COLUMN_CSPKG = "csPkg"
        private const val COLUMN_WAREHOUSE = "wareHouse"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_BARCODE TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_SOLOMON_ID TEXT, " +
                    "$COLUMN_UOM TEXT, $COLUMN_CSPKG INTEGER, $COLUMN_WAREHOUSE TEXT)"
        try {
            db?.execSQL(createTableQuery)
        } catch (e: SQLException) {
            e.printStackTrace()
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

    fun clearProducts() {
        val db = writableDatabase
        try {
//            db.delete(TABLE_NAME, "warehouse = ?", arrayOf(warehouse))
            db.delete(TABLE_NAME, null, null)
        } catch (e: SQLException){
            e.printStackTrace()
        } finally{
            db.close()
        }
    }

    fun syncProducts(products: List<Products>) {
        val db = writableDatabase
        try {
            for (product in products) {
                val values = ContentValues().apply {
                    put(COLUMN_BARCODE, product.barcode)
                    put(COLUMN_DESCRIPTION, product.description)
                    put(COLUMN_SOLOMON_ID, product.solomonID)
                    put(COLUMN_UOM, product.uom)
                    put(COLUMN_CSPKG, product.csPkg)
                    put(COLUMN_WAREHOUSE, product.wareHouse)
                }
                db.insert(TABLE_NAME, null, values)
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    fun getSolomonID(barcode: String): String {
        val db = readableDatabase
        val query = "SELECT $COLUMN_SOLOMON_ID FROM $TABLE_NAME WHERE $COLUMN_BARCODE = ?"
        val cursor = db.rawQuery(query, arrayOf(barcode))

        cursor.use {
            if(cursor.moveToFirst()) {
                val solomonID = cursor.getString(cursor.getColumnIndexOrThrow("solomonID"))

                if (cursor.moveToNext()) {
                    return "multi"
                }

                return solomonID
            }
        }

        return "none"
    }

}