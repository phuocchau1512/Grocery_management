package com.example.grocerymanagement.presentation.fragments.listFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.FragmentListRecipesBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.activity.SearchActivity
import com.example.grocerymanagement.presentation.activity.SuggestingChatActivity
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnRecipeInteractionListener
import com.example.grocerymanagement.presentation.adapter.RecipesAdapter
import com.example.grocerymanagement.presentation.fragments.DetailsRecipeFragment
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel


class ListRecipesFragment : Fragment(), OnRecipeInteractionListener {

    private var _binding: FragmentListRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel
    private lateinit var adapter: RecipesAdapter

    private var currentPage = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
        adapter = RecipesAdapter(mutableListOf(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Nếu đã cuộn đến gần cuối và không đang loading
                if (!isLoading && lastVisibleItem >= totalItemCount - 1) {
                    isLoading = true
                    // Thêm một item progress vào adapter khi đang load thêm dữ liệu
                    adapter.addFooterLoading()
                    viewModel.getRecipes(++currentPage, 5)
                }
            }
        })


        viewModel.recipes.observe(viewLifecycleOwner) { recipesList ->
            binding.progressBar.visibility = View.GONE

            adapter.removeFooterLoading()

            if (recipesList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE

                // Lọc ra các recipe chưa có trong adapter
                val existingIds = adapter.getRecipeIds()
                val newRecipes = recipesList.filterNot { existingIds.contains(it.id) }

                if (newRecipes.isNotEmpty()) {
                    adapter.addRecipes(newRecipes)
                    isLoading = false
                } else {
                    isLoading = true // Không tải tiếp nữa nếu không có gì mới
                }

                if (recipesList.size < 5) {
                    isLoading = true
                }
            }
        }
        settingMenu()

        viewModel.getRecipes(currentPage, 6)
    }

    private fun settingMenu(){
        // MENU xử lý
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_recipe, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> {
                        val intent = Intent(requireContext(), SearchActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.action_light -> {
                        val intent = Intent(requireContext(), SuggestingChatActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLikeClicked(recipe: Recipe) {
        val newFavorite = !recipe.isLiked
        val newLikes = if (newFavorite) recipe.likes + 1 else recipe.likes - 1
        val updatedItem = recipe.copy(isLiked = newFavorite, likes = newLikes)

        val newList = adapter.getCurrentList().map {
            if (it.id == recipe.id) updatedItem else it
        }

        viewModel.addLike(recipe.id)
        adapter.updateData(newList)
    }



    override fun onRecipeClicked(recipe: Recipe) {
        val fragment = DetailsRecipeFragment()

        fragment.arguments = Bundle().apply {
            putParcelable("selected_recipe", recipe)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
