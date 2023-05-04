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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import online.mohmedbakr.newsfeed.R
import online.mohmedbakr.newsfeed.data.dataStore.filterPreferencesDataStore
import online.mohmedbakr.newsfeed.databinding.ActivityMainBinding
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkRequest: NetworkRequest
    private lateinit var connectionCallback: ConnectionCallback

    var progressColor by Delegates.notNull<Int>()
    val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(filterPreferencesDataStore)
    }


    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        observeNetworkState()
        chickNightMode()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_newsfeed, R.id.navigation_bookmark
            )
        )


        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }


    private fun chickNightMode() {
        when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                progressColor = resources.getColor(R.color.white, this.theme)

            }

            Configuration.UI_MODE_NIGHT_NO -> {
                progressColor = resources.getColor(R.color.black, this.theme)
            }

            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                progressColor = resources.getColor(R.color.primary, this.theme)
            }
        }
    }

    private fun observeNetworkState() {
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectionCallback = ConnectionCallback(viewModel)
        connectivityManager.run {
            registerNetworkCallback(networkRequest, connectionCallback)
        }
        if (connectivityManager.activeNetwork == null)
            viewModel.networkState.value = false
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp()

    }

}