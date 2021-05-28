package kg.forestry.localstorage.db


import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DatabaseHelper(val context: Context) {

    private val undeletableTables = mutableListOf<String>()

    fun clearDatabaseTables() {
        clearTables(AppDatabase.DB_NAME)
    }

    private fun clearTables(database: String) {
        val db = context.openOrCreateDatabase(database, Context.MODE_PRIVATE, null, null)
        var query = "SELECT name FROM sqlite_master" +
                " WHERE type='table' " +
                "AND name!='android_metadata' " +
                "AND name!='room_master_table'" +
                "AND name!='room_master_table'"
        undeletableTables.forEach { query = "$query AND name!='$it'" }
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast) {
                clearTableByCheckingExistence(db, cursor.getString(0))
                cursor.moveToNext()
            }
        cursor.close()
        db.close()
    }

    private fun clearTableByCheckingExistence(db: SQLiteDatabase, tableName: String) {
        try {
            db.execSQL("DELETE FROM $tableName")
        } catch (e: Throwable) {
        }
    }
}