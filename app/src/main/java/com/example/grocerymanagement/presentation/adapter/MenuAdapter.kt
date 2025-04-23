package com.example.grocerymanagement.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.databinding.MenuItemBinding
import com.example.grocerymanagement.presentation.adapter.adapterItem.MenuItem

class MenuAdapter(private val items: List<MenuItem>, private val onClick: (MenuItem) -> Unit) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem) {
            binding.itemTitle.text = item.title
            binding.itemIcon.setImageResource(item.icon)
            binding.root.setOnClickListener { onClick(item) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}
