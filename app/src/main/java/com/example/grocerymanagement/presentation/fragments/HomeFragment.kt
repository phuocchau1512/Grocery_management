package com.example.grocerymanagement.presentation.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.FragmentHomeBinding
import com.example.grocerymanagement.presentation.adapter.adapterItem.MenuItem
import com.example.grocerymanagement.presentation.activity.InventoryActivity
import com.example.grocerymanagement.presentation.activity.RecipesActivity
import com.example.grocerymanagement.presentation.activity.RecipesManagerActivity
import com.example.grocerymanagement.presentation.activity.ShoppingActivity
import com.example.grocerymanagement.presentation.adapter.MenuAdapter


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!  // Non-null binding



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createView()
    }



    private fun createView(){
        binding.recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val spacing = 24
                if (parent.getChildAdapterPosition(view) % 2 == 0) {
                    outRect.right = spacing / 2
                } else {
                    outRect.left = spacing / 2
                }
                outRect.bottom = spacing
            }
        })

        val menuItems = listOf(
            MenuItem("Quản lý kho", R.drawable.baseline_inventory_24),
            MenuItem("Danh sách mua sắm", R.drawable.baseline_checklist_24),
            MenuItem("Quản lý công thức", R.drawable.baseline_book_24),
            MenuItem("Danh sách công thức", R.drawable.baseline_menu_book_24)
        )

// Xử lý khi click vào item
        val adapter = MenuAdapter(menuItems) { item ->
            when (item.title) {
                "Quản lý kho" -> {
                    val intent = Intent(requireContext(), InventoryActivity::class.java)
                    startActivity(intent)
                }
                "Danh sách mua sắm" -> {
                    val intent = Intent(requireContext(), ShoppingActivity::class.java)
                    startActivity(intent)
                }
                "Quản lý công thức" -> {
                    val intent = Intent(requireContext(),RecipesManagerActivity::class.java)
                    startActivity(intent)
                }
                "Danh sách công thức" -> {
                    val intent = Intent(requireContext(),RecipesActivity::class.java)
                    startActivity(intent)
                }
                else -> {
                }
            }
        }


        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter
    }



}