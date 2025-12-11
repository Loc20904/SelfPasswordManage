package com.example.test1.Fragments

import Account
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            // Click vào account -> mở EditAccountFragment
            val bundle = Bundle().apply {
                putString("account_id", account.id)
            }
//            val editFragment = EditAccountFragment().apply {
//                arguments = bundle
//            }
//            replaceFragment(editFragment)
        }

        binding.rvAccounts.apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
        Account("Gmail", "nguyenvana@gmail.com", "nguyenvana123", "••••••••"),
        Account("Shopee", "0851234567", "shopee_user", "••••••••"),
        Account("Bank Vietcombank", "0851234567", "vcb_user", "••••••••")
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
