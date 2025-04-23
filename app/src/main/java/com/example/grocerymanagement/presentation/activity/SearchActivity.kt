package com.example.grocerymanagement.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.ActivityRecipesBinding
import com.example.grocerymanagement.databinding.ActivitySearchBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnRecipeInteractionListener
import com.example.grocerymanagement.presentation.adapter.RecipesAdapter
import com.example.grocerymanagement.presentation.fragments.SearchFragment
import com.example.grocerymanagement.presentation.fragments.listFragment.ListRecipesFragment
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel

class SearchActivity : AppCompatActivity() {

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
            replaceFragment(SearchFragment())
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