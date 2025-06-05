package com.example.edupresence.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.edupresence.model.Class
import com.example.edupresence.model.User
import com.google.firebase.firestore.FirebaseFirestore
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore,
    private val supabase: Postgrest
) {

    fun getUsersByClassId(classId: Int): LiveData<List<User>> {
        val usersLiveData = MutableLiveData<List<User>>()

        firestore.collection("users")
            .whereEqualTo("classId", classId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    usersLiveData.value = emptyList()
                    return@addSnapshotListener
                }

                val userList = snapshot?.toObjects(User::class.java) ?: emptyList()
                usersLiveData.value = userList
            }

        return usersLiveData
    }

    suspend fun getClassById(classId: Int): Class? {
        return supabase
            .from("classes")
            .select {
                filter { Class::id eq classId }
            }
            .decodeSingleOrNull()
    }
}
