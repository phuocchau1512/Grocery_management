package com.example.grocerymanagement.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.databinding.ItemShoppingListBinding
import com.example.grocerymanagement.domain.model.ShoppingListItem
import java.text.SimpleDateFormat
import java.util.Locale

class ShoppingListAdapter(
    private var list: List<ShoppingListItem>,
    private val listener: OnShoppingListClickListener
) : RecyclerView.Adapter<ShoppingListAdapter.ShoppingListViewHolder>() {

    class ShoppingListViewHolder(val binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val shoppingList = list[position]
        with(holder.binding) {
            txtListName.text = shoppingList.name

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(shoppingList.createdAt)
            txtDateCreated.text = date?.let { outputFormat.format(it) }
            txtItemCount.text = shoppingList.itemCount.toString()

            // Set icon trái tim theo trạng thái
            val favoriteIcon = if (shoppingList.isFavorite == 1) {
                com.example.grocerymanagement.R.drawable.baseline_favorite_24
            } else {
                com.example.grocerymanagement.R.drawable.baseline_favorite_border_24
            }
            imgFavorite.setImageResource(favoriteIcon)

            // Xử lý click icon trái tim
            imgFavorite.setOnClickListener {
                listener.onFavoriteClick(shoppingList)
            }

            // Click toàn item
            root.setOnClickListener {
                listener.onListClick(shoppingList)
            }
        }
    }


    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<ShoppingListItem>) {
        val diffCallback = ShoppingListDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun getListAt(position: Int): ShoppingListItem = list[position]
    fun getCurrentList():List<ShoppingListItem> = list

    fun removeItem(position: Int) {
        val mutableList = list.toMutableList() // Chuyển sang MutableList để có thể xóa
        mutableList.removeAt(position)
        list = mutableList
        notifyItemRemoved(position)
    }
}

class ShoppingListDiffCallback(
    private val oldList: List<ShoppingListItem>,
    private val newList: List<ShoppingListItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].id == newList[newPos].id
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}

interface OnShoppingListClickListener {
    fun onListClick(list: ShoppingListItem)
    fun onFavoriteClick(list: ShoppingListItem) // thêm dòng này
}
