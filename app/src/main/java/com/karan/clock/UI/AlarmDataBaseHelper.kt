package com.karan.clock.UI

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.karan.clock.MainActivity

class AlarmDataBaseHelper private constructor(context: Context): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "AlarmDatabase.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "Alarms"
        private const val COLUMN_ID = "_id"
        private const val COLUMN_HOURS = "hours"
        private const val COLUMN_MINUTES = "minutes"
        private const val COLUMN_LABEL = "lable"
        private const val COLUMN_STATUS = "status"

        private var INSTANCE : AlarmDataBaseHelper? = null

        fun getInstance(context: Context? = null) : AlarmDataBaseHelper {                            //  Damn !!!!
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AlarmDataBaseHelper(context!!.applicationContext).also { INSTANCE = it }
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_HOURS INTEGER,
            $COLUMN_MINUTES INTEGER,
            $COLUMN_LABEL TEXT,
            $COLUMN_STATUS INTEGER
            );
        """
        db?.execSQL(query)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }



    fun insertAlarm(hour : Int, minutes : Int, label : String, isActive : Int) {
        val db = writableDatabase
        val content = ContentValues().apply {
            put(COLUMN_HOURS, hour)
            put(COLUMN_MINUTES, minutes)
            put(COLUMN_LABEL, label)
            put(COLUMN_STATUS, isActive)
        }
        db.insert(TABLE_NAME,null,content)
        db.close()
    }

    fun getAllAlarms() : MutableList<Alarm> {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"
        val allAlarms = mutableListOf<Alarm>()
        val cursor = db.rawQuery(query,null)

        while(cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val hour = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOURS))
            val min = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTES))
            val label = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL))
            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))

            allAlarms.add(Alarm(id,hour,min,label,isActive))
        }
        cursor.close()
        db.close()
        return allAlarms
    }

    fun updateAlarm(id : Int, hour : Int?, minutes : Int?, label : String?) : Int {

        val cv = ContentValues()
        if(hour != null) cv.put(COLUMN_HOURS, hour)
        if(minutes != null) cv.put(COLUMN_MINUTES, minutes)
        if(label != null) cv.put(COLUMN_LABEL, label)

        return writableDatabase.update(TABLE_NAME,cv,"_id=?", arrayOf(id.toString()))
    }



    fun deleteAlarm(id: Int) : Int {
        return writableDatabase.delete(TABLE_NAME,"_id=?", arrayOf(id.toString()))
    }

    fun getAlarmById(id : Int) : Alarm {

        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $id"
        val cursor = readableDatabase.rawQuery(query, null)
        cursor.moveToFirst()
        val alarm = Alarm(
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HOURS)),
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MINUTES)),
            cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LABEL)),
            cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS))
        )
        cursor.close()
        return alarm
    }

    fun switchAlarm(id : Int, state : Int) {
        val query = "UPDATE $TABLE_NAME SET $COLUMN_STATUS = ? WHERE $COLUMN_ID = ?;"
        writableDatabase.execSQL(query, arrayOf(state,id))
    }

}