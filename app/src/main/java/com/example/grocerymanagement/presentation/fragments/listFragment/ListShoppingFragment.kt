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
import com.example.grocerymanagement.databinding.FragmentListShoppingBinding
import com.example.grocerymanagement.domain.model.ShoppingListItem
import com.example.grocerymanagement.presentation.activity.ShoppingActivity
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnShoppingListClickListener
import com.example.grocerymanagement.presentation.adapter.ShoppingListAdapter
import com.example.grocerymanagement.presentation.fragments.addFragment.AddShoppingListFragment
import com.example.grocerymanagement.presentation.viewModel.ShoppingViewModel


class ListShoppingFragment : Fragment(), OnShoppingListClickListener {

    private var _binding: FragmentListShoppingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListShoppingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        adapter = ShoppingListAdapter(emptyList(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))

        settingSwipeDelete()

        viewModel.shoppingList.observe(viewLifecycleOwner) { shoppingList ->
            binding.progressBar.visibility = View.GONE
            if (shoppingList.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
            adapter.updateData(shoppingList)
        }
        viewModel.getShoppingLists()

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
                        if (currentFragment is AddShoppingListFragment) {
                            Toast.makeText(requireContext(), "Bạn đang ở phần thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                        } else {
                            (requireActivity() as ShoppingActivity).replaceFragment(AddShoppingListFragment())
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
                val list = adapter.getListAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa danh sách này?")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteShoppingList(list.id)
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

    override fun onListClick(list: ShoppingListItem) {
        val fragment = ListShoppingItemFragment()

        fragment.arguments = Bundle().apply {
            putParcelable("selected_list", list)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onFavoriteClick(list: ShoppingListItem) {
        val newFavorite = if (list.isFavorite == 1) 0 else 1
        val updatedItem = list.copy(isFavorite = newFavorite)
        val newList = adapter.getCurrentList().map {
            if (it.id == list.id) updatedItem else it
        }
        viewModel.addFav(list.id)
        adapter.updateData(newList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
