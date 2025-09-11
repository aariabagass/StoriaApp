package com.ariabagas.storiaapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ariabagas.storiaapp.databinding.ActivityHomeBinding
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.ui.welcome.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : ComponentActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val prefs: UserPreference by inject()
    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: StoryAdapter

    private var shouldScrollToTop = true

    private var scrollJob: Job? = null

    private val launcherAddStory = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            shouldScrollToTop = true
            viewModel.refreshStories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
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

        adapter = StoryAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        binding.swipeRefresh.setOnRefreshListener {
            shouldScrollToTop = true
            viewModel.refreshStories()
        }

        adapter.onItemClick = { story, imgPhoto, tvName, tvDesc ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_ID, story.id)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                androidx.core.util.Pair(imgPhoto, ViewCompat.getTransitionName(imgPhoto)!!),
                androidx.core.util.Pair(tvName, ViewCompat.getTransitionName(tvName)!!),
                androidx.core.util.Pair(tvDesc, ViewCompat.getTransitionName(tvDesc)!!)
            )

            startActivity(intent, options.toBundle())
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                val isLoading = loadStates.refresh is androidx.paging.LoadState.Loading
                binding.swipeRefresh.isRefreshing = isLoading

                if (loadStates.refresh is androidx.paging.LoadState.NotLoading && shouldScrollToTop) {
                    shouldScrollToTop = false

                    scrollJob?.cancel()
                    scrollJob = lifecycleScope.launch {
                        delay(1000L)
                        binding.recyclerView.post {
                            binding.recyclerView.scrollToPosition(0)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            prefs.tokenFlow().collect { token ->
                if (!token.isNullOrBlank()) {
                    viewModel.getStories(token).observe(this@HomeActivity) { pagingData ->
                        adapter.submitData(lifecycle, pagingData)
                    }
                }
            }
        }

        binding.btnMaps.setOnClickListener {
            startActivity(Intent(this, MapStoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                prefs.clearToken()
                runOnUiThread {
                    Toast.makeText(this@HomeActivity, "Logged out", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
            }
        }
        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            launcherAddStory.launch(intent)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    override fun onDestroy() {
        scrollJob?.cancel()
        super.onDestroy()
    }
}

