package com.example.edupresence

import android.app.Application
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class EduPresenceApplication : Application() {

    val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = "https://eglqipkefxbygdjixspr.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVnbHFpcGtlZnhieWdkaml4c3ByIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDkwMTk5OTksImV4cCI6MjA2NDU5NTk5OX0.8c39UyejtStDk1gUXVUUOgszA4XxBeWP9rGt2cav3X8"
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi lain seperti Firebase Analytics bisa ditambahkan di sini
    }
}