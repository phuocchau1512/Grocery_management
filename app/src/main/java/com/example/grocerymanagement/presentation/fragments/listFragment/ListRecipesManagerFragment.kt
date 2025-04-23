package com.example.grocerymanagement.presentation.fragments.listFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.FragmentListRecipesManagerBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.activity.RecipesManagerActivity
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnRecipeClickListener
import com.example.grocerymanagement.presentation.adapter.RecipesMangerAdapter
import com.example.grocerymanagement.presentation.fragments.addFragment.AddRecipesFragment
import com.example.grocerymanagement.presentation.fragments.editFragment.EditRecipesFragment
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel


class ListRecipesManagerFragment : Fragment(), OnRecipeClickListener {


    private var _binding: FragmentListRecipesManagerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel
    private lateinit var adapter: RecipesMangerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecipesManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
        adapter = RecipesMangerAdapter(emptyList(), this)


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))

        settingSwipeDelete()

        viewModel.userRecipes.observe(viewLifecycleOwner) { recipesList ->
            binding.progressBar.visibility = View.GONE
            if (recipesList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
            adapter.updateData(recipesList)
        }
        viewModel.getUserRecipes()

        settingMenu()

    }

    private fun settingMenu(){
        // MENU xử lý
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.activity_shopping, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_add -> {
                        val currentFragment = parentFragmentManager.findFragmentById(R.id.frameContainer)
                        if (currentFragment is AddRecipesFragment) {
                            Toast.makeText(requireContext(), "Bạn đang ở phần thêm công thức!", Toast.LENGTH_SHORT).show()
                        } else {
                            (requireActivity() as RecipesManagerActivity).replaceFragment(
                                AddRecipesFragment()
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun settingSwipeDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val recipe = adapter.getRecipeAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa danh sách này?")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteRecipe(recipe.id)
                        adapter.removeItem(position)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Hủy") { dialog, _ ->
                        dialog.dismiss()
                        adapter.notifyDataSetChanged()
                    }
                    .show()
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecipeClick(recipe: Recipe) {
        val fragment = EditRecipesFragment()

        fragment.arguments = Bundle().apply {
            putParcelable("selected_recipe", recipe)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


}