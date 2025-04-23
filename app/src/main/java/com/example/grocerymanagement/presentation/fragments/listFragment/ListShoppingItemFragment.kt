package com.example.grocerymanagement.presentation.fragments.listFragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.FragmentListShoppingItemBinding
import com.example.grocerymanagement.domain.model.Product
import com.example.grocerymanagement.domain.model.ShoppingListItem
import com.example.grocerymanagement.presentation.activity.CustomScannerActivity
import com.example.grocerymanagement.presentation.adapter.ItemSpacingDecoration
import com.example.grocerymanagement.presentation.adapter.OnShoppingListItemClickListener
import com.example.grocerymanagement.presentation.adapter.ShoppingListItemAdapter
import com.example.grocerymanagement.presentation.adapter.adapterItem.ShoppingItemProduct
import com.example.grocerymanagement.presentation.fragments.addFragment.AddShoppingItemFragment
import com.example.grocerymanagement.presentation.fragments.addFragment.AddShoppingItemPublicFragment
import com.example.grocerymanagement.presentation.viewModel.ShoppingViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


class ListShoppingItemFragment : Fragment() , OnShoppingListItemClickListener {


    private var _binding: FragmentListShoppingItemBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShoppingViewModel
    private lateinit var adapter: ShoppingListItemAdapter

    private var selectedList: ShoppingListItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListShoppingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ShoppingViewModel::class.java]
        adapter = ShoppingListItemAdapter(emptyList(), this)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(ItemSpacingDecoration(24))

        selectedList = arguments?.getParcelable("selected_list")

        settingSwipeDelete()

        viewModel.shoppingListItem.observe(viewLifecycleOwner) { shoppingListItem ->
            binding.progressBar.visibility = View.GONE
            if (shoppingListItem.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
            adapter.updateData(shoppingListItem)
        }
        viewModel.getShoppingItem(selectedList!!.id)

        // MENU xử lý
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.activity_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_qr_scan -> {
                        checkPermissionAndScan()
                        true
                    }
                    R.id.action_add -> {
                        val currentFragment = parentFragmentManager.findFragmentById(R.id.frameContainer)
                        if (currentFragment is AddShoppingItemFragment) {
                            Toast.makeText(requireContext(), "Bạn đang ở phần thêm sản phẩm!", Toast.LENGTH_SHORT).show()
                        } else {
                            val fragment = AddShoppingItemFragment()
                            val bundle = Bundle()
                            bundle.putInt("list_id", selectedList!!.id)
                            fragment.arguments = bundle
                            replaceFragment(fragment)
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
                val item = adapter.getItemAt(position)

                AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa sản phẩm này?")
                    .setPositiveButton("Xóa") { _, _ ->
                        viewModel.deleteShoppingListItem(item.id,item.productId)
                        viewModel.getShoppingItem(selectedList!!.id)
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

    private val scanLauncher = registerForActivityResult(ScanContract()) { result ->
        val barcode = result.contents
        if (barcode == null) {
            Toast.makeText(requireContext(), "Không có dữ liệu!", Toast.LENGTH_SHORT).show()
            return@registerForActivityResult
        }

        viewModel.getProductFromServer(barcode).observe(viewLifecycleOwner) { serverProduct ->
            if (serverProduct != null) {
                val fragment = AddShoppingItemPublicFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selected_product", Product.coverInfo(serverProduct))
                        putInt("shopping_list_id", selectedList!!.id)
                    }
                }
                replaceFragment(fragment)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Thông báo")
                    .setMessage("Sản phẩm không tồn tại. Vui lòng thêm thủ công!\nMã: $barcode")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }



    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scanCode()
        } else {
            Toast.makeText(requireContext(), "Cần cấp quyền camera!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scanCode()
        } else {
            requestCameraPermission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun scanCode() {
        val options = ScanOptions().apply {
            setPrompt("Đưa mã vào giữa màn hình")
            setBeepEnabled(true)
            setOrientationLocked(true)
            setCaptureActivity(CustomScannerActivity::class.java)
            setCameraId(0)
        }
        scanLauncher.launch(options)
    }

    // Hàm chuyển đổi Fragment
    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckClick(item: ShoppingItemProduct) {
        if (item.isChecked == 0) {
            AlertDialog.Builder(requireContext())
                .setTitle("Thêm vào kho?")
                .setMessage("Bạn có muốn thêm sản phẩm '${item.productName}' vào kho không?")
                .setPositiveButton("Có") { _, _ ->
                    item.isChecked = 1
                    viewModel.addCheck(item.id)
                    viewModel.addProductToList(item.productId, item.quantity)
                }
                .setNegativeButton("Không") { _, _ ->
                    // Không check thì cần update lại UI
                    item.isChecked = 1
                    viewModel.addCheck(item.id)
                    adapter.notifyDataSetChanged()
                }
                .show()
        } else {
            // Bỏ chọn
            item.isChecked = 0
            viewModel.addCheck(item.id)
        }
    }




}