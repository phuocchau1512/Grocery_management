package com.example.grocerymanagement.presentation.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.ActivityRecipesBinding
import com.example.grocerymanagement.databinding.ActivityShoppingBinding
import com.example.grocerymanagement.presentation.fragments.listFragment.ListRecipesFragment
import com.example.grocerymanagement.presentation.fragments.listFragment.ListShoppingFragment
import com.example.grocerymanagement.presentation.viewModel.ShoppingViewModel

class RecipesActivity : AppCompatActivity() {


    private lateinit var binding: ActivityRecipesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipesBinding.inflate(layoutInflater)
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
            replaceFragment(ListRecipesFragment())
        }
    }



    private fun handleBackPress() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
        invalidateOptionsMenu()
    }

}