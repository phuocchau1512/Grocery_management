package com.example.grocerymanagement.presentation.fragments.addFragment

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
import com.example.grocerymanagement.databinding.FragmentAddRecipesBinding
import com.example.grocerymanagement.presentation.viewModel.RecipesViewModel
import java.io.File


class AddRecipesFragment : Fragment() {


    private var _binding: FragmentAddRecipesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RecipesViewModel  // Khai báo ViewModel
    private  var selectedListId:Int? = null

    private var selectedImageUri: Uri? = null

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
        _binding = FragmentAddRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

        selectedListId = arguments?.getInt("list_id")

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


            if (selectedImageUri == null) {
                Toast.makeText(requireContext(), "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Chuyển Uri thành File
            val imgFile = uriToFile(selectedImageUri!!)

            turnOffSaveBtn()
            viewModel.addRecipes( name, ingredients, instructions, time, imgFile)
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
