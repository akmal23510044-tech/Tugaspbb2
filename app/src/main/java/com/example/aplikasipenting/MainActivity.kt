package com.example.aplikasipenting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.aplikasipenting.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var credentialManager: CredentialManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Gunakan agar status bar transparan di Android 13+
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inset handling untuk menghindari overlapping status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val request = null
        credentialManager = CredentialManager.create(this@MainActivity)
        auth = FirebaseAuth.getInstance()

        registerEvent()
    }

    private fun registerEvent() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val request = prepareRequest()
                    loginByGoogle(request)
                } catch (e: Exception) {
                    Log.e("MainActivity", "registerEvent error", e)
                    Toast.makeText(
                        this@MainActivity,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun prepareRequest(): GetCredentialRequest {
        // Pastikan ini adalah Web Client ID dari Firebase console (OAuth 2.0 client ID)
        val serverClientId =
            "518345984735-7nkicv0m0pkjgmglq4818831ucirimmo.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    private suspend fun loginByGoogle(request: GetCredentialRequest) {
        try {
            // Gunakan context yang eksplisit
            val result = credentialManager.getCredential(this@MainActivity, request)
            val credential = result.credential

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            val idToken = googleIdTokenCredential.idToken

            if (idToken.isNullOrEmpty()) {
                Toast.makeText(this, "Gagal mendapatkan ID token", Toast.LENGTH_SHORT).show()
                return
            }

            firebaseLoginCallback(idToken)
        } catch (e: NoCredentialException) {
            Toast.makeText(this, "Tidak ada kredensial ditemukan", Toast.LENGTH_SHORT).show()
        } catch (e: GetCredentialException) {
            Toast.makeText(this, "Gagal mengambil kredensial: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "GetCredentialException", e)
        } catch (e: Exception) {
            Log.e("MainActivity", "loginByGoogle error", e)
            Toast.makeText(this, "Login gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun firebaseLoginCallback(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_LONG).show()
                    toMyPage()
                } else {
                    val msg = task.exception?.message ?: "Unknown error"
                    Toast.makeText(this, "Login Gagal: $msg", Toast.LENGTH_SHORT).show()
                    Log.w("MainActivity", "signInWithCredential failed: $msg", task.exception)
                }
            }
    }

    private fun isAuthenticated(): Boolean =
        auth.currentUser != null

    override fun onStart() {
        super.onStart()
        if (isAuthenticated()) {
            toMyPage()
        }
    }

    private fun toMyPage() {
        val intent = Intent(this, TodoActivity::class.java)
        startActivity(intent)
        finish()
    }
}
