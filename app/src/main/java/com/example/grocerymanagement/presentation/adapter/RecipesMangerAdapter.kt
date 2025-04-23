package com.example.grocerymanagement.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.databinding.ItemRecipeCardBinding
import com.example.grocerymanagement.domain.model.Recipe

class RecipesMangerAdapter(
    private var recipeList: List<Recipe>,
    private val listener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipesMangerAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(val binding: ItemRecipeCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.binding.txtTitle.text = recipe.title
        holder.binding.txtLikes.text = recipe.likes.toString()
        holder.binding.txtTime.text = recipe.timeMinutes.toString()

        Glide.with(holder.itemView.context)
            .load(RetrofitClient.getBaseUrl() + recipe.imageUrl)
            .placeholder(R.drawable.baseline_image_24)
            .error(R.drawable.baseline_image_24)
            .into(holder.binding.imgDish)

        holder.itemView.setOnClickListener {
            listener.onRecipeClick(recipe)
        }

        holder.binding.imgDish.setImageResource(
            if (recipe.isLiked) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )
    }

    override fun getItemCount(): Int = recipeList.size

    fun updateData(newList: List<Recipe>) {
        val diffCallback = RecipeDiffCallback(recipeList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        recipeList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(position: Int) {
        val mutableList = recipeList.toMutableList()
        mutableList.removeAt(position)
        recipeList = mutableList
        notifyItemRemoved(position)
    }

    fun getRecipeAt(position: Int): Recipe {
        return recipeList[position]
    }


}

interface OnRecipeClickListener {
    fun onRecipeClick(recipe: Recipe)
}


class RecipeDiffCallback(
    private val oldList: List<Recipe>,
    private val newList: List<Recipe>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

