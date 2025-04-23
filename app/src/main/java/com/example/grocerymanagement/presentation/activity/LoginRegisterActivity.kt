package com.example.grocerymanagement.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.grocerymanagement.R
import com.example.grocerymanagement.presentation.fragments.LoginFragment


class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        // Hiển thị LoginFragment mặc định
        if (savedInstanceState == null) {
            replaceFragment(LoginFragment())
        }

    }

    // Hàm thay thế fragment trong FrameLayout
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
}
