package com.example.edupresence.repository

import com.example.edupresence.model.AttendanceRecord
import com.example.edupresence.model.AttendanceSession
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createSession(session: AttendanceSession): Result<String> {
        return try {
            val docRef = db.collection("attendance_sessions").document()
            val newSession = session.copy(sessionId = docRef.id)
            docRef.set(newSession).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordAttendance(record: AttendanceRecord): Result<String> {
        return try {
            val docRef = db.collection("attendance_records").document()
            val newRecord = record.copy(recordId = docRef.id)
            docRef.set(newRecord).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSessionsForTeacher(teacherId: String): Result<List<AttendanceSession>> {
        return try {
            val snapshot = db.collection("attendance_sessions")
                .whereEqualTo("teacherId", teacherId)
                .get()
                .await()
            val sessions = snapshot.toObjects(AttendanceSession::class.java)
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecordsForStudent(studentId: String): Result<List<AttendanceRecord>> {
        return try {
            val snapshot = db.collection("attendance_records")
                .whereEqualTo("studentId", studentId)
                .get()
                .await()
            val records = snapshot.toObjects(AttendanceRecord::class.java)
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}