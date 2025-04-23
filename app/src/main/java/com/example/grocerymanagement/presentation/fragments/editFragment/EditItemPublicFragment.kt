package com.example.grocerymanagement.presentation.fragments.editFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.databinding.FragmentEditItemPublicBinding
import com.example.grocerymanagement.domain.model.Product
import com.example.grocerymanagement.presentation.viewModel.ProductViewModel


class EditItemPublicFragment : Fragment() {


    private var _binding: FragmentEditItemPublicBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProductViewModel  // Khai báo ViewModel

    private var selectedProduct: Product? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditItemPublicBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Lấy sản phẩm từ Bundle
        selectedProduct = arguments?.getParcelable("selected_product")

        selectedProduct?.let { product ->
            binding.etProductName.text = product.name
            binding.etBarcode.text = product.barcode
            binding.etDescription.text = product.description
            binding.etSoLuong.setText(product.quantity.toString())
            binding.etNote.setText(product.note)

            // Nếu có ảnh, hiển thị ảnh sản phẩm
            Glide.with(requireContext())
                .load(RetrofitClient.getBaseUrl() + product.img) // URL ảnh
                .placeholder(R.drawable.baseline_image_24) // Ảnh hiển thị khi tải
                .error(R.drawable.baseline_image_24) // Ảnh hiển thị khi lỗi
                .into(binding.imgProduct)
        }

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        binding.btnIncrease.setOnClickListener {
            var quantity = binding.etSoLuong.text.toString().toIntOrNull() ?: 1
            quantity++
            binding.etSoLuong.setText(quantity.toString())
        }

        binding.btnDecrease.setOnClickListener {
            var quantity = binding.etSoLuong.text.toString().toIntOrNull() ?: 1
            if (quantity > 1) {
                quantity--
            }
            binding.etSoLuong.setText(quantity.toString())
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()
            val barcode = binding.etBarcode.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val quantityStr = binding.etSoLuong.text.toString().trim()
            val note = binding.etNote.text.toString().trim()

            if (name.isEmpty() || barcode.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            turnOffSaveBtn()
            selectedProduct?.let { it1 -> viewModel.editPublicProductToInvent(it1.id,quantityStr,note) }
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { isSuccess ->
            turnOnSaveBtn()
            if (isSuccess) {
                Toast.makeText(requireContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

    }



    private fun turnOffSaveBtn(){
        binding.saveBtn.isEnabled=false
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun turnOnSaveBtn(){
        binding.saveBtn.isEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}