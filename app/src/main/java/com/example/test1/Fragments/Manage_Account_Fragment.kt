package com.example.test1.Fragments

import Account
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1.R
import com.example.test1.databinding.FragmentManageAccountBinding
import com.example.test1.adapters.AccountAdapter
import com.example.test1.utils.FileManager

class Manage_Account_Fragment : Fragment() {

    private var _binding: FragmentManageAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var accountAdapter: AccountAdapter
    private val accountList = mutableListOf<Account>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadAccounts()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(accountList) { account ->
            // Khi bấm vào 1 account
            showBiometricForAccount(account)
        }

        binding.rvAccounts.apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun showBiometricForAccount(account: Account) {
        val executor = ContextCompat.getMainExecutor(requireContext())

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Xác thực để xem mật khẩu")
            .setSubtitle("Dùng vân tay để mở tài khoản ${account.appName}")
            .setNegativeButtonText("Hủy")
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    showAccountDialog(account)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(requireContext(), "Có lỗi xảy ra !", Toast.LENGTH_SHORT).show()
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    private fun showAccountDialog(account: Account) {
        val message = """
        Ứng dụng: ${account.appName}
        Email/SĐT: ${account.username}
        Tài khoản: ${account.accountId}
        Mật khẩu: ${account.password}
    """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle("Chi tiết tài khoản")
            .setMessage(message)
            .setPositiveButton("Đóng", null)
            .setNegativeButton("Xóa tài khoản") { _, _ ->
                confirmDeleteAccount(account)
            }
            .show()
    }
    private fun confirmDeleteAccount(account: Account) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa tài khoản")
            .setMessage("Bạn có chắc muốn xóa tài khoản ${account.appName}?")
            .setPositiveButton("XÓA") { _, _ ->
                deleteAccount(account)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteAccount(account: Account) {
        // Xóa trong file
        val currentAccounts = FileManager.loadAccounts(requireContext())
        val updatedAccounts = currentAccounts.filter { it.id != account.id }
        FileManager.saveAccounts(requireContext(), updatedAccounts)

        // Nếu đang có list trên RecyclerView:
        accountList.removeAll { it.id == account.id }
        accountAdapter.notifyDataSetChanged()

        Toast.makeText(
            requireContext(),
            "✅ Đã xóa ${account.appName}",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun loadAccounts() {
        accountList.clear()
        accountList.addAll(FileManager.loadAccounts(requireContext()))
        accountAdapter.notifyDataSetChanged()
    }

    private fun setupClickListeners() {
        // Thêm tài khoản mới
//        binding.btnAddQuick.setOnClickListener {
//            replaceFragment(AddPasswordFragment())
//        }

        // Search (sau này bạn thêm filter)
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAccounts(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    private fun filterAccounts(query: String) {
        val allAccounts = FileManager.loadAccounts(requireContext())
        val filtered = allAccounts.filter { account ->
            account.appName.contains(query, ignoreCase = true) ||
                    account.username.contains(query, ignoreCase = true) ||
                    account.accountId.contains(query, ignoreCase = true)
        }
        accountList.clear()
        accountList.addAll(filtered)
        accountAdapter.notifyDataSetChanged()
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // đảm bảo activity có FrameLayout này
            .addToBackStack(null)
            .commit()
    }

    private fun getSampleAccounts() = listOf(
        Account("Facebook", "nguyenvana@gmail.com", "fb_user123", "••••••••"),
        Account("Gmail", "nguyenvana@gmail.com", "nguyenvana123", "••••••••")
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
