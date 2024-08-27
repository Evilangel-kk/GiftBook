package com.example.accounting

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class MyDataBaseHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {

    private val createRecordTable = ("create table RecordTable ("
            + "id TEXT PRIMARY KEY,"
            + "name TEXT NOT NULL,"
            + "price INTEGER NOT NULL,"
            + "event TEXT,"
            + "inORout INTEGER,"
            + "date TEXT"
            +");")

    override fun onCreate(p0: SQLiteDatabase?) {
        Log.d("DataBase", "ready Created")
        if (p0 != null) {
            p0.execSQL(createRecordTable)
            Log.d("DataBase", "createRecordTable Created")
        }else{
            Log.d("DataBase", "p0 == null")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    // 插入记录
    fun insertRecord(name: String, price: Int, event: String, inORout: Boolean, date: String): Long {
        val db = this.writableDatabase
        // 使用当前时间生成唯一的id
        val timestamp = System.currentTimeMillis().toString()
        val contentValues = ContentValues().apply {
            put("id",timestamp)
            put("name", name)
            put("price", price)
            put("event", event)
            put("inORout", if (inORout) 1 else 0)
            put("date", date)
        }
        return db.insert("RecordTable", null, contentValues)
    }

    // 删除记录
    fun deleteRecord(name: String, price: Int, event: String, inORout: Boolean, date: String): Int {
        val db = this.writableDatabase
        // 删除条件
        val selection = "name = ? AND price = ? AND event = ? AND inORout = ? AND date = ?"
        val selectionArgs = arrayOf(
            name,
            price.toString(),
            event,
            if (inORout) "1" else "0",
            date
        )
        // 执行删除操作
        return db.delete("RecordTable", selection, selectionArgs)
    }

    // 查询相关人支出总和
    fun getTotalExpenditureByName(name: String): Int {
        val db = this.readableDatabase
        val query = "SELECT SUM(price) FROM RecordTable WHERE name = ? AND inORout = 0"
        val cursor: Cursor = db.rawQuery(query, arrayOf(name))
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    // 查询相关人收入总和
    fun getTotalIncomeByName(name: String): Int {
        val db = this.readableDatabase
        val query = "SELECT SUM(price) FROM RecordTable WHERE name = ? AND inORout = 1"
        val cursor: Cursor = db.rawQuery(query, arrayOf(name))
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    // 查询所有记录
    @SuppressLint("Range")
    fun getSomeoneRecords(name: String): List<Record> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM RecordTable WHERE name = ?", arrayOf(name))
        val records = mutableListOf<Record>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val price = cursor.getInt(cursor.getColumnIndex("price"))
                val matter = cursor.getString(cursor.getColumnIndex("event"))
                val inORout = cursor.getInt(cursor.getColumnIndex("inORout")) == 1
                val date = cursor.getString(cursor.getColumnIndex("date"))

                val record = Record(id, name, price, matter, inORout, date)
                records.add(record)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return records
    }

    // 查询所有记录
    @SuppressLint("Range")
    fun getAllRecords(): List<Record> {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM RecordTable", null)
        val records = mutableListOf<Record>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getString(cursor.getColumnIndex("id"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val price = cursor.getInt(cursor.getColumnIndex("price"))
                val matter = cursor.getString(cursor.getColumnIndex("event"))
                val inORout = cursor.getInt(cursor.getColumnIndex("inORout")) == 1
                val date = cursor.getString(cursor.getColumnIndex("date"))

                val record = Record(id, name, price, matter, inORout, date)
                records.add(record)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return records
    }

    // 删除记录
    fun deleteRecordById(id: Int): Int {
        val db = this.writableDatabase
        return db.delete("RecordTable", "id = ?", arrayOf(id.toString()))
    }

}