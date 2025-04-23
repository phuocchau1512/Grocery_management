package com.example.grocerymanagement.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.databinding.FragmentSearchBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnRecipeInteractionListener
import com.example.grocerymanagement.presentation.adapter.RecipesAdapter
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel
import androidx.appcompat.widget.SearchView
import com.example.grocerymanagement.R


class SearchFragment : Fragment(), OnRecipeInteractionListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipesViewModel
    private lateinit var adapter: RecipesAdapter

    private var currentPage = 1
    private var isLoading = false
    private var keyword: String? = null

    companion object {
        private const val PAGE_SIZE = 5
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
        adapter = RecipesAdapter(mutableListOf(), this)

        setupRecyclerView()
        settingSearchView()
        observeViewModel()
    }



    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItem >= totalItemCount - 1) {
                    isLoading = true
                    adapter.addFooterLoading()
                    viewModel.searchRecipes(keyword!!, ++currentPage, PAGE_SIZE)
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.searchRecipes.observe(viewLifecycleOwner) { recipesList ->
            binding.progressBar.visibility = View.GONE
            adapter.removeFooterLoading()

            if (recipesList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE

                val existingIds = adapter.getRecipeIds()
                val newRecipes = recipesList.filterNot { existingIds.contains(it.id) }

                if (newRecipes.isNotEmpty()) {
                    adapter.addRecipes(newRecipes)
                    isLoading = false
                } else {
                    isLoading = true
                }

                if (recipesList.size < PAGE_SIZE) {
                    isLoading = true
                }
            }
        }
    }

    private fun searchRecipes(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        binding.tvEmpty.visibility = View.GONE

        viewModel.removeSearch()
        viewModel.searchRecipes(query, currentPage, PAGE_SIZE)
    }

    private fun settingSearchView() {
        binding.searchView.isIconified = false

        binding.searchView.post {
            val searchTextId = resources.getIdentifier("search_src_text", "id", requireContext().packageName)
            val editText = binding.searchView.findViewById<EditText>(searchTextId)
            editText.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.searchView.setOnCloseListener {
            binding.searchView.setQuery("", false)
            binding.searchView.isIconified = false
            binding.searchView.post {
                val searchTextId = resources.getIdentifier("search_src_text", "id", requireContext().packageName)
                val editText = binding.searchView.findViewById<EditText>(searchTextId)
                editText.setText("")
                editText.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
            false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        keyword = it
                        currentPage = 1
                        searchRecipes(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.length >= 3) {
                        keyword = it
                        currentPage = 1
                        searchRecipes(it)
                    } else if (it.isEmpty()) {
                        keyword = null
                        currentPage = 1
                        adapter.updateData(emptyList())
                        binding.recyclerView.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                        viewModel.removeSearch()
                    }
                }
                return true
            }
        })
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
