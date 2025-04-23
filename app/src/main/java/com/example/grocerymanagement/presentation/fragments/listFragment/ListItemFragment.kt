package com.example.grocerymanagement.presentation.fragments.listFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.domain.model.Product
import com.example.grocerymanagement.databinding.FragmentListItemBinding
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnItemClickListener
import com.example.grocerymanagement.presentation.adapter.ProductAdapter
import com.example.grocerymanagement.presentation.fragments.editFragment.EditItemFragment
import com.example.grocerymanagement.presentation.fragments.editFragment.EditItemPublicFragment
import com.example.grocerymanagement.presentation.viewModel.InventoryViewModel

class ListItemFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentListItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: InventoryViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[InventoryViewModel::class.java]
        adapter = ProductAdapter(emptyList(), this)

        settingSwipeDelete()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))

        viewModel.product.observe(viewLifecycleOwner) { productList ->
            binding.progressBar.visibility = View.GONE
            if (productList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
            adapter.updateData(productList)
        }

        viewModel.getProductInInvent()
    }

    private fun settingSwipeDelete() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val productToDelete = adapter.getProductAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteProductInInvent(productToDelete.id.toString())
                        adapter.removeItem(position)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Hủy") { dialog, _ ->
                        dialog.dismiss()
                        adapter.notifyDataSetChanged() // Khôi phục lại hiển thị
                    }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onItemClick(product: Product) {
        val fragment = if (product.is_private == 0) {
            EditItemPublicFragment()
        } else {
            EditItemFragment()
        }

        fragment.arguments = Bundle().apply {
            putParcelable("selected_product", product)
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
