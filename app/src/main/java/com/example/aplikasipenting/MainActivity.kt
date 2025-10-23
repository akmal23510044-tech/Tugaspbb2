package com.example.aplikasipenting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.aplikasipenting.databinding.ActivityMainBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        credentialManager = CredentialManager.create(this)
        auth = Firebase.auth

        registerEvent()
    }

    private fun registerEvent() {
        binding.btnLogin.setOnClickListener {
            lifecycleScope.launch {
                    val request = prepareRequest()
                    loginByGoogle(request)

            }
        }
    }

    fun prepareRequest(): GetCredentialRequest {
        // Pastikan ini adalah Web Client ID dari Firebase console (OAuth 2.0 client ID)
        val serverClientId =
            "518345984735-7nkicv0m0pkjgmglq4818831ucirimmo.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest
            .Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return request
    }

    suspend fun loginByGoogle(request: GetCredentialRequest) {
        try {
            // Gunakan context yang eksplisit
            val result = credentialManager.getCredential(
                context = this ,
                request = request
            )
            val credential = result.credential
            val idToken = GoogleIdTokenCredential.createFrom(credential.data)

            firebaseLoginCallback(idToken.idToken)

        } catch (exc: NoCredentialException) {
            Toast.makeText(this, "login gagal" + exc.message, Toast.LENGTH_SHORT).show()
        } catch (exc: Exception) {
            Toast.makeText(this, "Login gagal"+ exc.message, Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun isAuthenticated(): Boolean {
        return auth.currentUser != null
    }
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
