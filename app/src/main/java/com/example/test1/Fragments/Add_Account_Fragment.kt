package com.example.test1.Fragments

import Account
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test1.databinding.FragmentAddAccountBinding
import com.example.test1.utils.FileManager
import java.util.UUID

class Add_Account_Fragment : Fragment() {

    private var _binding: FragmentAddAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            val appName = binding.etAppName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val accountId = binding.etAccountId.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Validation
            if (appName.isEmpty() || username.isEmpty() || accountId.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "⚠️ Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tạo account mới
            val newAccount = Account(
                appName = appName,
                username = username,
                accountId = accountId,
                password = password,
                id = UUID.randomUUID().toString()
            )

            // Load accounts hiện tại + thêm mới + save
            val currentAccounts = FileManager.loadAccounts(requireContext())
            val updatedAccounts = currentAccounts + newAccount
            FileManager.saveAccounts(requireContext(), updatedAccounts)

            Toast.makeText(context, "✅ Đã lưu $appName!", Toast.LENGTH_LONG).show()

            // Quay về ManageAccountFragment
            parentFragmentManager.popBackStack()
        }

        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
