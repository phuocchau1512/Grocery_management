package com.example.grocerymanagement.presentation.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.grocerymanagement.presentation.activity.MainActivity
import com.example.grocerymanagement.R
import com.example.grocerymanagement.domain.model.LoginRequest
import com.example.grocerymanagement.databinding.FragmentLoginBinding
import com.example.grocerymanagement.presentation.viewModel.LoginViewModel


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!  // Non-null binding
    private lateinit var loginViewModel: LoginViewModel  // Khai báo ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ViewModel
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // Lắng nghe trạng thái đăng nhập
        loginViewModel.loginStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                // Chuyển sang MainActivity và kết thúc LoginActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()  // Đóng Activity hiện tại
            } else {
                turnOffProgressBar()
                Toast.makeText(requireContext(), "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
        }

        // Xử lý sự kiện nhấn nút đăng nhập
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val password = binding.loginPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                turnProgressBar()
                loginViewModel.loginUser(LoginRequest(email, password))
            } else {

                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
            }
        }

        // Chuyển sang màn hình đăng ký
        binding.createAccount.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameContainer, RegisterFragment())
                    .commit()

        }


    }

    private fun turnProgressBar(){
        binding.progressBar.visibility = View.VISIBLE
        binding.loginBtn.isEnabled = false
    }

    private fun turnOffProgressBar(){
        binding.progressBar.visibility = View.INVISIBLE
        binding.loginBtn.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}