package com.example.test1.utils

import Account
import android.content.Context
import org.json.JSONArray
import java.io.File
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import android.util.Base64
import java.util.*

object FileManager {
    private const val FILE_NAME = "accounts.json"
    private const val DIR_NAME = "Passwords"
    private const val KEY = "MySecretKey12345" // 16 bytes cho AES-128

    fun getAccountsFile(context: Context): File {
        val dir = File(context.getExternalFilesDir(null), DIR_NAME)
        dir.mkdirs()
        return File(dir, FILE_NAME)
    }

    fun saveAccounts(context: Context, accounts: List<Account>) {
        try {
            val jsonArray = JSONArray().apply {
                accounts.forEach { account ->
                    put(org.json.JSONObject().apply {
                        put("id", account.id)
                        put("appName", account.appName)
                        put("username", account.username)
                        put("accountId", account.accountId)
                        put("password", account.password)
                    })
                }
            }

            val encrypted = encrypt(jsonArray.toString())
            getAccountsFile(context).writeText(encrypted)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadAccounts(context: Context): List<Account> {
        return try {
            val file = getAccountsFile(context)
            if (!file.exists()) return getSampleAccounts()

            val encrypted = file.readText()
            val jsonString = decrypt(encrypted)
            val jsonArray = JSONArray(jsonString)

            (0 until jsonArray.length()).mapNotNull { i ->
                val jsonObj = jsonArray.getJSONObject(i)
                Account(
                    jsonObj.getString("appName"),
                    jsonObj.getString("username"),
                    jsonObj.getString("accountId"),
                    jsonObj.getString("password"),
                    jsonObj.getString("id")
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            getSampleAccounts()
        }
    }

    private fun encrypt(data: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(KEY.toByteArray(), "AES"))
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    private fun decrypt(encryptedData: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(KEY.toByteArray(), "AES"))
        val decrypted = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT))
        return String(decrypted, Charsets.UTF_8)
    }

    private fun getSampleAccounts() = listOf(
        Account("Facebook", "nguyenvana@gmail.com", "fb_user123", "••••••••"),
        Account("Gmail", "nguyenvana@gmail.com", "nguyenvana123", "••••••••"),
        Account("Shopee", "0851234567", "shopee_user", "••••••••"),
        Account("Bank Vietcombank", "0851234567", "vcb_user", "••••••••")
    )
}
