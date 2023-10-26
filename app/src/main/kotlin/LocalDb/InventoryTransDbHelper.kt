package LocalDb

import MssqlCon.PublicVars
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.sql.SQLException

class InventoryTransDbHelper (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = PublicVars.DATABASE_NAME
        private val DATABASE_VERSION = PublicVars.DATABASE_VERSION

        private const val TABLE_NAME = "inventory_trans"
        private const val COLUMN_ID = "id"
        private const val COLUMN_BARCODE = "barcode"
        private const val COLUMN_SOLOMON_ID = "solomonID"
        private const val COLUMN_UOM = "uom"
        private const val COLUMN_QTY = "qty"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_DATE_ENTRY = "date_entry"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_REFNBR = "refNbr"
        private const val COLUMN_REMARKS = "remarks"
        private const val COLUMN_WAREHOUSE = "wareHouse"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_BARCODE TEXT, $COLUMN_SOLOMON_ID TEXT, $COLUMN_UOM TEXT, $COLUMN_QTY INTEGER, $COLUMN_DATE TEXT, " +
                    "$COLUMN_DATE_ENTRY TEXT, $COLUMN_USERNAME TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_REFNBR TEXT, $COLUMN_REMARKS TEXT, $COLUMN_WAREHOUSE TEXT)"
        try {
            db?.execSQL(createTable)
        } catch (e: SQLException) { e.printStackTrace() }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS TABLE_NAME"
        try {
            db?.execSQL(dropTableQuery)
            onCreate(db)
        } catch (e: android.database.SQLException) {
            e.printStackTrace();
        }
    }
}