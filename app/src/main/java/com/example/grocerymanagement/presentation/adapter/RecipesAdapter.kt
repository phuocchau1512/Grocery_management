package com.example.grocerymanagement.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.databinding.ItemLoadingBinding
import com.example.grocerymanagement.databinding.ItemRecipeCardBinding
import com.example.grocerymanagement.domain.model.Recipe

class RecipesAdapter(
    private var recipeList: MutableList<Recipe>,
    private val listener: OnRecipeInteractionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 1
    private val TYPE_LOADING = 2
    private var isLoading = false

    inner class RecipeViewHolder(val binding: ItemRecipeCardBinding) : RecyclerView.ViewHolder(binding.root)
    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            RecipeViewHolder(binding)
        } else {
            val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LoadingViewHolder(binding)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RecipeViewHolder) {
            val recipe = recipeList[position]
            val context = holder.itemView.context

            with(holder.binding) {
                txtTitle.text = recipe.title
                txtLikes.text = recipe.likes.toString()
                txtTime.text = recipe.timeMinutes.toString()

                Glide.with(context)
                    .load(RetrofitClient.getBaseUrl() + recipe.imageUrl)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.baseline_image_24)
                    .into(imgDish)

                // Set icon trái tim theo trạng thái
                val favoriteIcon = if (recipe.isLiked) {
                    com.example.grocerymanagement.R.drawable.baseline_favorite_24
                } else {
                    com.example.grocerymanagement.R.drawable.baseline_favorite_border_24
                }
                imgLike.setImageResource(favoriteIcon)

                imgLike.setOnClickListener {
                    listener.onLikeClicked(recipe)
                }

                root.setOnClickListener {
                    listener.onRecipeClicked(recipe)
                }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == recipeList.size - 1) TYPE_LOADING else TYPE_ITEM
    }


    override fun getItemCount(): Int = recipeList.size

    fun updateData(newList: List<Recipe>) {
        val diffCallback = RecipeDiffCallback(recipeList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        recipeList = newList.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(position: Int) {
        val mutableList = recipeList.toMutableList()
        mutableList.removeAt(position)
        recipeList = mutableList
        notifyItemRemoved(position)
    }

    fun getRecipeIds(): List<Int> {
        return recipeList.map { it.id }
    }


    fun addRecipes(newList: List<Recipe>) {
        val updatedList = recipeList.toMutableList()
        updatedList.addAll(newList)

        val diffCallback = RecipeDiffCallback(recipeList, updatedList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        recipeList = updatedList
        diffResult.dispatchUpdatesTo(this)
    }

    fun addFooterLoading() {
        if (!isLoading) {
            isLoading = true
            recipeList.add(Recipe(0, "", "", "", "", "", 0, 0, 0, false)) // Dummy item
            notifyItemInserted(recipeList.size - 1)
        }
    }

    fun removeFooterLoading() {
        if (isLoading && recipeList.isNotEmpty()) {
            val position = recipeList.size - 1
            recipeList.removeAt(position)
            notifyItemRemoved(position)
            isLoading = false
        }
    }

    fun getCurrentList(): List<Recipe> {
        return recipeList
    }



}

interface OnRecipeInteractionListener {
    fun onLikeClicked(recipe: Recipe)
    fun onRecipeClicked(recipe: Recipe)
}