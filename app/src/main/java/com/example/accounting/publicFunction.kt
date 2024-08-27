package com.example.accounting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface publicFunction {
    companion object {
        fun getCurrentDate(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }

        @SuppressLint("Range")
        fun exportToExcel(context: Context) {
            val db = MyDataBaseHelper(context, "recordData.db", 1)
            val dbReadable = db.readableDatabase

            // Fetch data from database
            val cursor = dbReadable.rawQuery("SELECT * FROM RecordTable", null)

            val workbook: Workbook = XSSFWorkbook()
            val sheet: Sheet = workbook.createSheet("Records")

            // Create header row
            val headerRow: Row = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("ID")
            headerRow.createCell(1).setCellValue("Name")
            headerRow.createCell(2).setCellValue("Price")
            headerRow.createCell(3).setCellValue("Event")
            headerRow.createCell(4).setCellValue("InOrOut")
            headerRow.createCell(5).setCellValue("Date")

            var rowIndex = 1
            while (cursor.moveToNext()) {
                val row: Row = sheet.createRow(rowIndex++)
                row.createCell(0).setCellValue(cursor.getString(cursor.getColumnIndex("id")))
                row.createCell(1).setCellValue(cursor.getString(cursor.getColumnIndex("name")))
                row.createCell(2).setCellValue(cursor.getInt(cursor.getColumnIndex("price")).toDouble())
                row.createCell(3).setCellValue(cursor.getString(cursor.getColumnIndex("event")))
                row.createCell(4).setCellValue(cursor.getInt(cursor.getColumnIndex("inORout")).toDouble())
                row.createCell(5).setCellValue(cursor.getString(cursor.getColumnIndex("date")))
            }
            cursor.close()

            // Save file
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Records.xlsx")
            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }
            workbook.close()

            // Share file
            val fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant temporary read permission
                setPackage("com.tencent.mm") // WeChat package name
            }
            context.startActivity(Intent.createChooser(intent, "Share Excel File"))
        }
    }
}