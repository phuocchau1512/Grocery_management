package com.example.grocerymanagement.presentation.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.ActivityRecipesManagerBinding
import com.example.grocerymanagement.presentation.fragments.listFragment.ListRecipesManagerFragment

class RecipesManagerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRecipesManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.appBar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        // Nút back trên toolbar
        binding.appBar.toolbar.setNavigationOnClickListener { handleBackPress() }

        // Back trên thiết bị
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackPress()
            }
        })

        if (savedInstanceState == null) {
            replaceFragment(ListRecipesManagerFragment())
        }
    }



    private fun handleBackPress() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    internal fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
        invalidateOptionsMenu()
    }

}