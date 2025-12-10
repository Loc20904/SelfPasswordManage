package com.example.test1.Fragments

import Account
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test1.R
import com.example.test1.databinding.FragmentManageAccountBinding
import com.example.test1.adapters.AccountAdapter

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
        accountList.addAll(getSampleAccounts())
        accountAdapter.notifyDataSetChanged()
    }

    private fun setupClickListeners() {
        // Thêm tài khoản mới
//        binding.btnAddQuick.setOnClickListener {
//            replaceFragment(AddPasswordFragment())
//        }

        // Search (sau này bạn thêm filter)
        binding.etSearch.setOnClickListener {
            binding.etSearch.requestFocus()
        }
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
