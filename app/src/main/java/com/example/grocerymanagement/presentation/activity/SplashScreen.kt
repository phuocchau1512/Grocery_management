package com.example.grocerymanagement.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerymanagement.R

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Lấy SharedPreferences
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPref.getString("userID", null) // Kiểm tra userID

        // Chuyển hướng sau 2 giây
        Handler(Looper.getMainLooper()).postDelayed({
            if (userId != null) {
                // Nếu đã đăng nhập → vào MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Nếu chưa đăng nhập → vào LoginRegisterActivity
                startActivity(Intent(this, LoginRegisterActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
