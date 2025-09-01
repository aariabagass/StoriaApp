package com.ariabagas.storiaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.ariabagas.storiaapp.data.local.datastore.UserPreference
import com.ariabagas.storiaapp.ui.home.HomeActivity
import com.ariabagas.storiaapp.ui.welcome.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val prefs: UserPreference by inject()
    @Volatile
    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { isLoading }

        CoroutineScope(Dispatchers.IO).launch {
            val token = prefs.getToken()
            val destination = if (token.isNullOrEmpty()) {
                LoginActivity::class.java
            } else {
                HomeActivity::class.java
            }

            isLoading = false
            startActivity(Intent(this@MainActivity, destination).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }
}
