package com.example.test1.Fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.test1.MainActivity
import com.example.test1.R
import com.example.test1.R.string.old_pin_wrong
import com.example.test1.databinding.FragmentChangePinBinding

class fragment_change_pin : Fragment() {

        private var _binding: FragmentChangePinBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentChangePinBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.btnBackMain.setOnClickListener {
                (activity as? MainActivity)?.GoToMainScreen()
            }

            binding.btnSavePin.setOnClickListener {
                changePin()
            }
        }

        private fun changePin() = with(binding) {
            val oldPin = etOldPin.text.toString()
            val newPin = etNewPin.text.toString()
            val confirmPin = etConfirmPin.text.toString()

            val storedPin = loadPin()
            if (storedPin == null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Chưa thiết lập PIN"
                return
            }

            if (oldPin != storedPin) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.old_pin_wrong)
                return
            }

            if (newPin.length != 6 || confirmPin.length != 6 || newPin != confirmPin) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = getString(R.string.pin_not_match)
                return
            }

            savePin(newPin)
            Toast.makeText(requireContext(), R.string.pin_saved, Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        // các hàm prefs – có thể copy từ LoginFragment hoặc tách ra file chung
        private fun getPinPrefs(): SharedPreferences {
            val masterKey = MasterKey.Builder(requireContext())
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                requireContext(),
                "pin_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }

        private fun loadPin(): String? =
            getPinPrefs().getString("USER_PIN", null)

        private fun savePin(pin: String) {
            getPinPrefs().edit().putString("USER_PIN", pin).apply()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
