package com.example.grocerymanagement.presentation.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.databinding.FragmentDetailsRecipeBinding
import com.example.grocerymanagement.databinding.FragmentEditRecipesBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel
import java.io.File


class DetailsRecipeFragment : Fragment() {

    private var _binding: FragmentDetailsRecipeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel  // Khai báo ViewModel


    private var selectedRecipe: Recipe? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Lấy sản phẩm từ Bundle
        selectedRecipe = arguments?.getParcelable("selected_recipe")

        selectedRecipe?.let { recipe ->
            binding.txtTitle.text = recipe.title
            binding.txtIngredients.text = recipe.ingredients
            binding.txtTime.text = recipe.timeMinutes.toString()
            binding.txtInstruction.text = recipe.instructions

            // Nếu có ảnh, hiển thị ảnh sản phẩm
            Glide.with(requireContext())
                .load(RetrofitClient.getBaseUrl() + recipe.imageUrl) // URL ảnh
                .placeholder(R.drawable.baseline_image_24) // Ảnh hiển thị khi tải
                .error(R.drawable.baseline_image_24) // Ảnh hiển thị khi lỗi
                .into(binding.imgProduct)
        }

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}