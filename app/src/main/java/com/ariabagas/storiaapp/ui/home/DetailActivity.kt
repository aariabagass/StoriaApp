package com.ariabagas.storiaapp.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.ariabagas.storiaapp.databinding.ActivityDetailBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.utils.NotifUtils
import com.ariabagas.storiaapp.utils.ResultState
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch


class DetailActivity : ComponentActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModel()
    private val prefs: UserPreference by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        val storyId = intent.getStringExtra(EXTRA_ID) ?: return
        ViewCompat.setTransitionName(binding.ivStory, "photo_$storyId")
        ViewCompat.setTransitionName(binding.tvName, "name_$storyId")
        ViewCompat.setTransitionName(binding.tvDesc, "desc_$storyId")

        lifecycleScope.launch {
            prefs.tokenFlow().collect { token ->
                if (!token.isNullOrBlank()) {
                    viewModel.loadStoryDetail(token, storyId)
                }
            }
        }

        viewModel.storyDetail.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showLoading(false)
                    val story = result.data
                    binding.tvName.text = story.name
                    binding.tvDesc.text = story.description
                    Glide.with(this).load(story.photoUrl).into(binding.ivStory)
                }
                is ResultState.Error -> {
                    showLoading(false)
                    showToast("Failed: ${result.message}")
                    NotifUtils.show(this, "Please check your Internet Connection", isError = true)
                }
            }
        }

    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
