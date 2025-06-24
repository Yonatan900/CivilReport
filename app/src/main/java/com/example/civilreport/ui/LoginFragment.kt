package com.example.civilreport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.civilreport.R
import com.example.civilreport.databinding.LoginFragmentBinding
import com.example.civilreport.util.Error
import com.example.civilreport.util.Loading
import com.example.civilreport.util.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!
    val viewModel: LoginViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //signIn button
        binding.btnLogin.setOnClickListener {
            val email = binding.etUsername.text.toString()
            val pass  = binding.etPassword.text.toString()
            if (email.isBlank() || pass.isBlank()) {
                Toast.makeText(requireContext(), "Enter both email and password", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.signIn(email, pass)
            }
        }

        viewModel.loginResult.observe(viewLifecycleOwner) {

            when(it.status) {

                is Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
                }
                is Success -> {
                    findNavController().navigate(R.id.action_loginFragment_to_reportsFragment)
                }
                is Error -> {
                    Toast.makeText(requireContext(),it.status.message,Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}