package online.mohmedbakr.newsfeed.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import online.mohmedbakr.newsfeed.R
import online.mohmedbakr.newsfeed.data.datasources.local.filterPreferencesDataStore
import online.mohmedbakr.newsfeed.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var connectionCallback: ConnectionCallback

    var progressColor by Delegates.notNull<Int>()

    val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(filterPreferencesDataStore)
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // The app targets SDK 36, so position the UI explicitly around system bars.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        chickNightMode()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemBarInsets()
        observeNetworkState()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_newsfeed, R.id.navigation_bookmark)
        )

        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            windowInsets
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun chickNightMode() {
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                progressColor = resources.getColor(R.color.white, theme)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                progressColor = resources.getColor(R.color.black, theme)
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                progressColor = resources.getColor(R.color.primary, theme)
            }
        }
    }

    private fun observeNetworkState() {
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectionCallback = ConnectionCallback(viewModel)
        connectivityManager.registerNetworkCallback(networkRequest, connectionCallback)

        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = activeNetwork?.let(connectivityManager::getNetworkCapabilities)
        viewModel.setNetworkState(
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
        )
    }

    override fun onDestroy() {
        if (::connectionCallback.isInitialized) {
            connectivityManager.unregisterNetworkCallback(connectionCallback)
        }
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()
    }
}
