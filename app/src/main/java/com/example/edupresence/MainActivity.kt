package com.example.edupresence

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onAttendClick(view: android.view.View) {
        // Logika absensi akan ditambahkan nanti
    }
}