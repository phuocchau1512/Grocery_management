package com.example.grocerymanagement.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.ItemShoppingBinding
import com.example.grocerymanagement.presentation.adapter.adapterItem.ShoppingItemProduct
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.presentation.fragments.listFragment.ListShoppingItemFragment

// Adapter cho danh sách mua sắm
class ShoppingListItemAdapter(
    private var list: List<ShoppingItemProduct>,
    private val listener: OnShoppingListItemClickListener
) : RecyclerView.Adapter<ShoppingListItemAdapter.ViewHolder>() {

    // ViewHolder để hiển thị từng item trong danh sách
    inner class ViewHolder(val binding: ItemShoppingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShoppingItemProduct) {
            // Cập nhật dữ liệu vào view
            binding.txtProductName.text = item.productName
            binding.txtBarcode.text = item.barcode ?: ""
            binding.txtQuantity.text = item.quantity.toString()

            Glide.with(binding.root.context)
                .load(RetrofitClient.getBaseUrl() + (item.img ?: ""))
                .placeholder(R.drawable.baseline_image_24)
                .error(R.drawable.baseline_image_24)
                .into(binding.imgProduct)

            binding.checkboxPurchased.setOnCheckedChangeListener(null)
            binding.checkboxPurchased.isChecked = item.isChecked == 1

            binding.checkboxPurchased.setOnCheckedChangeListener { _, _ ->
                listener.onCheckClick(item)
            }


        }
    }

    // Tạo mới ViewHolder từ layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemShoppingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Gắn dữ liệu vào ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])  // Truyền đúng item vào bind
    }

    // Trả về số lượng item trong danh sách
    override fun getItemCount(): Int = list.size

    // Cập nhật dữ liệu mới cho adapter với việc sử dụng DiffUtil
    fun updateData(newList: List<ShoppingItemProduct>) {
        val diffCallback = ShoppingItemProductDiffCallback(list, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    // Lấy các items đã được đánh dấu là mua
    fun getCheckedItems(): List<ShoppingItemProduct> {
        return list.filter { it.isChecked == 1 }
    }

    fun removeItem(position: Int) {
        val mutableList = list.toMutableList() // Chuyển sang MutableList để có thể xóa
        mutableList.removeAt(position)
        list = mutableList
        notifyItemRemoved(position)
    }

    fun getItemAt(position: Int) = list[position]
}

// Interface để lắng nghe sự kiện click vào checkbox
interface OnShoppingListItemClickListener {
    fun onCheckClick(item: ShoppingItemProduct)
}

// DiffUtil.Callback để so sánh dữ liệu trong adapter
class ShoppingItemProductDiffCallback(
    private val oldList: List<ShoppingItemProduct>,
    private val newList: List<ShoppingItemProduct>
) : DiffUtil.Callback() {

    // Trả về số lượng item trong danh sách cũ
    override fun getOldListSize(): Int = oldList.size

    // Trả về số lượng item trong danh sách mới
    override fun getNewListSize(): Int = newList.size

    // So sánh xem item có cùng id không
    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].id == newList[newPos].id
    }

    // So sánh toàn bộ nội dung của item
    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}
