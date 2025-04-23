package com.example.grocerymanagement.presentation.fragments.editFragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.grocerymanagement.R
import com.example.grocerymanagement.data.source.retrofit.RetrofitClient
import com.example.grocerymanagement.databinding.FragmentEditRecipesBinding
import com.example.grocerymanagement.domain.model.Recipe
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel
import java.io.File


class EditRecipesFragment : Fragment() {



    private var _binding: FragmentEditRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel  // Khai báo ViewModel

    private var selectedImageUri: Uri? = null

    private var selectedRecipe: Recipe? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it // Lưu Uri
                binding.imgProduct.setImageURI(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Lấy sản phẩm từ Bundle
        selectedRecipe = arguments?.getParcelable("selected_recipe")

        selectedRecipe?.let { recipe ->
            binding.edtTitle.setText(recipe.title)
            binding.edtIngredients.setText(recipe.ingredients)
            binding.edtTime.setText(recipe.timeMinutes.toString())
            binding.edtInstructions.setText(recipe.instructions)

            // Nếu có ảnh, hiển thị ảnh sản phẩm
            Glide.with(requireContext())
                .load(RetrofitClient.getBaseUrl() + recipe.imageUrl) // URL ảnh
                .placeholder(R.drawable.baseline_image_24) // Ảnh hiển thị khi tải
                .error(R.drawable.baseline_image_24) // Ảnh hiển thị khi lỗi
                .into(binding.imgProduct)
        }

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

        // Xử lý khi bấm nút chọn ảnh
        binding.btnPickImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }


        binding.saveBtn.setOnClickListener {
            val name = binding.edtTitle.text.toString().trim()
            val time = binding.edtTime.text.toString().trim()
            val ingredients = binding.edtIngredients.text.toString().trim()
            val instructions = binding.edtInstructions.text.toString().trim()

            if (name.isEmpty() || time.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            turnOffSaveBtn()

            if (selectedImageUri != null) {
                val imageFile = uriToFile(selectedImageUri!!)
                viewModel.editRecipe(selectedRecipe!!.id, name, ingredients, instructions, time, imageFile)
            } else {
                viewModel.editRecipe(selectedRecipe!!.id, name, ingredients, instructions, time)
            }
        }


        viewModel.saveStatus.observe(viewLifecycleOwner) { isSuccess ->
            turnOnSaveBtn()
            if (isSuccess) {
                Toast.makeText(requireContext(), "Lưu thành công!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
        }

    }

    private fun uriToFile(uri: Uri): File {
        val file = File(requireContext().cacheDir, "temp_image.jpg")
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
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