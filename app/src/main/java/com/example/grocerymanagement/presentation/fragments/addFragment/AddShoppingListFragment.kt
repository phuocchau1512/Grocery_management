package com.example.grocerymanagement.presentation.fragments.addFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.grocerymanagement.R
import com.example.grocerymanagement.databinding.FragmentAddItemPublicBinding
import com.example.grocerymanagement.databinding.FragmentAddShoppingListBinding
import com.example.grocerymanagement.presentation.viewModel.ProductViewModel
import com.example.grocerymanagement.presentation.viewModel.ShoppingViewModel


class AddShoppingListFragment : Fragment() {

    private var _binding: FragmentAddShoppingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ShoppingViewModel  // Khai báo ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddShoppingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[ShoppingViewModel::class.java]


        binding.saveBtn.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()


            if (name.isEmpty() ) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addNewList(name)
        }

        viewModel.saveStatus.observe(viewLifecycleOwner) { isSuccess ->
            turnOnSaveBtn()
            if (isSuccess) {
                Toast.makeText(requireContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

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