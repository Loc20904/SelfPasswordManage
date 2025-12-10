package com.example.test1

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.test1.Fragments.ui.FullscreenFragment
import com.example.test1.Fragments.ui.login.LoginFragment
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.test1.Fragments.Add_Account_Fragment
import com.example.test1.Fragments.Manage_Account_Fragment
import com.example.test1.Fragments.fragment_change_pin

class MainActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
        setupBiometric()
    }

    public fun GoToMainScreen(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FullscreenFragment())
            .addToBackStack(null)
            .commit()
    }

    public fun GoToPinChange(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment_change_pin())
            .addToBackStack(null)
            .commit()
    }

    public fun GoToView(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Manage_Account_Fragment())
            .addToBackStack(null)
            .commit()
    }

    public fun GoToAdd(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Add_Account_Fragment())
            .addToBackStack(null)
            .commit()
    }


    private fun canUseBiometric(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG
        )) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    private fun setupBiometric() {
        val executor = ContextCompat.getMainExecutor(this)

        biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    GoToMainScreen()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    // TODO: báo lỗi hoặc fallback sang login thường
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // TODO: báo vân tay không khớp
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Đăng nhập bằng vân tay")
            .setSubtitle("Dùng vân tay để mở khóa ứng dụng")
            .setNegativeButtonText("Dùng mật khẩu")
            .build()
    }

    fun startBiometric() {
        if (canUseBiometric()) {
            biometricPrompt.authenticate(promptInfo)
        } else {
            // fallback: mở màn login user/pass bình thường
        }
    }
}