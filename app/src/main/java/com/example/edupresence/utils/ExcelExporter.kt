package com.example.edupresence.utils

import com.example.edupresence.model.AttendanceRecord
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ExcelExporter {
    fun exportToExcel(records: List<AttendanceRecord>, file: File): Result<Boolean> {
        return try {
            val workbook: Workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Attendance")

            val header = sheet.createRow(0)
            header.createCell(0).setCellValue("Record ID")
            header.createCell(1).setCellValue("Session ID")
            header.createCell(2).setCellValue("Student ID")
            header.createCell(3).setCellValue("Timestamp")
            header.createCell(4).setCellValue("Status")
            header.createCell(5).setCellValue("Location")

            records.forEachIndexed { index, record ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(record.recordId)
                row.createCell(1).setCellValue(record.sessionId)
                row.createCell(2).setCellValue(record.studentId)
                row.createCell(3).setCellValue(record.timestamp.toString())
                row.createCell(4).setCellValue(record.status)
                row.createCell(5).setCellValue(record.location)
            }

            FileOutputStream(file).use { fileOut ->
                workbook.write(fileOut)
            }
            workbook.close()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}