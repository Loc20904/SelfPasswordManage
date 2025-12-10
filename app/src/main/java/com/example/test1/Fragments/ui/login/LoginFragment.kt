package com.example.test1.Fragments.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.test1.MainActivity
import com.example.test1.databinding.FragmentLoginBinding
import com.example.test1.R
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isPinVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (loadPin() == null) {
            savePin("123456")
            Toast.makeText(requireContext(), "PIN mặc định: 123456", Toast.LENGTH_SHORT).show()
        }

        // Vân tay (nếu dùng)
        binding.btnFingerprint.setOnClickListener {
            (activity as? MainActivity)?.startBiometric()
        }

        binding.btnTogglePin.setOnClickListener {
            togglePinVisibility()
        }

        setupPinInputs()

    }

    private fun setPinVisible(visible: Boolean) {
        val edits = listOf(binding.pin1, binding.pin2, binding.pin3,
            binding.pin4, binding.pin5, binding.pin6)

        edits.forEach { et ->
            val selection = et.text?.length ?: 0
            et.inputType = if (visible) {
                InputType.TYPE_CLASS_NUMBER          // hiện số
            } else {
                InputType.TYPE_CLASS_NUMBER or
                InputType.TYPE_NUMBER_VARIATION_PASSWORD // ẩn số (••••)
            }
            et.setSelection(selection)
        }
    }

    private fun togglePinVisibility() {
        isPinVisible = !isPinVisible
        setPinVisible(isPinVisible)

        // đổi icon mắt nếu muốn
        val resId = if (isPinVisible) R.drawable.ic_visibility_off_30
        else R.drawable.ic_visibility_30
        binding.btnTogglePin.setImageResource(resId)
    }

    private fun setupPinInputs() = with(binding) {
        val edits = listOf(pin1, pin2, pin3, pin4, pin5, pin6)

        fun moveFocus(from: Int, forward: Boolean) {
            val next = if (forward) from + 1 else from - 1
            if (next in edits.indices) {
                edits[next].requestFocus()
            }
        }

        edits.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                private var beforeText = ""

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    beforeText = s?.toString() ?: ""
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) { }

                override fun afterTextChanged(s: Editable?) {
                    val text = s?.toString() ?: ""

                    // Gõ mới 1 ký tự (từ rỗng -> 1 ký tự)
                    if (beforeText.isEmpty() && text.length == 1) {
                        if (index < edits.lastIndex) {
                            moveFocus(index, forward = true)
                        } else {
                            checkPin()   // ô cuối
                        }
                    }

                    // Xóa (từ 1 ký tự -> rỗng)
                    if (beforeText.length == 1 && text.isEmpty()) {
                        if (index > 0) {
                            moveFocus(index, forward = false)
                        }
                    }
                }
            })
        }

        // focus ô đầu tiên khi mở màn
        pin1.requestFocus()
    }

    private fun getEnteredPin(): String = with(binding) {
        "${pin1.text}${pin2.text}${pin3.text}${pin4.text}${pin5.text}${pin6.text}"
    }

    private fun checkPin() {
        val pin = getEnteredPin()
        if (pin.length < 6) return

        val storedPin = loadPin()
        if (storedPin == null) {
            Toast.makeText(requireContext(), "Chưa thiết lập PIN", Toast.LENGTH_SHORT).show()
            return
        }

        if (pin == storedPin) {
            Toast.makeText(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.GoToMainScreen()
        } else {
            Toast.makeText(requireContext(), R.string.invalid_pin, Toast.LENGTH_SHORT).show()
            clearPin()
        }
    }

    private fun clearPin() = with(binding) {
        pin1.text?.clear()
        pin2.text?.clear()
        pin3.text?.clear()
        pin4.text?.clear()
        pin5.text?.clear()
        pin6.text?.clear()
        pin1.requestFocus()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //Pin setting up
    private fun getPinPrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            requireContext(),
            "pin_prefs",                  // tên file XML
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun savePin(pin: String) {
        val prefs = getPinPrefs()
        prefs.edit().putString("USER_PIN", pin).apply()
    }

    private fun loadPin(): String? {
        val prefs = getPinPrefs()
        return prefs.getString("USER_PIN", null)
    }
}
