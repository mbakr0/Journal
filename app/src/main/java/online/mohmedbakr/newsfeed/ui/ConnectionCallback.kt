package online.mohmedbakr.newsfeed.ui

import android.net.ConnectivityManager
import android.net.Network

class ConnectionCallback(private val viewModel: MainViewModel) :
    ConnectivityManager.NetworkCallback() {
    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        viewModel.setNetworkState(true)

    }

    override fun onLost(network: Network) {
        super.onLost(network)
        viewModel.setNetworkState(false)

    }


}