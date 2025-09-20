package com.example.reservasi_meja

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Perbaikan: Tidak perlu setContentView, karena background sudah diatur di tema
        // Perbaikan: Menggunakan postDelayed untuk transisi yang lebih mulus
        Handler(Looper.getMainLooper()).postDelayed({
            // Perbaikan: Mengambil instance Firebase Auth sekali di luar if/else
            val auth = FirebaseAuth.getInstance()
            val intent = if (auth.currentUser != null) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 2000)
    }
}