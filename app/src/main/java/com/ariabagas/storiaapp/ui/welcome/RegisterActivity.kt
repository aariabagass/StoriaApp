package com.ariabagas.storiaapp.ui.welcome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.ariabagas.storiaapp.R
import com.ariabagas.storiaapp.databinding.ActivityRegisterBinding
import com.ariabagas.storiaapp.utils.NotifUtils
import com.ariabagas.storiaapp.utils.ResultState
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : ComponentActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            viewModel.register(name, email, password)
        }

        binding.btnLogin.setOnClickListener {
            onBackPressed()
        }

        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    showLoading(false)
                    Toast.makeText(this@RegisterActivity, "Account Created", Toast.LENGTH_SHORT).show()
                    runOnUiThread {
                        startActivity(Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                    }
                }

                is ResultState.Error -> {
                    showLoading(false)
                    NotifUtils.show(this, result.message, isError = true)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }
}