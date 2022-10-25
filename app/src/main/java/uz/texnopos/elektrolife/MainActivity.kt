package uz.texnopos.elektrolife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.databinding.ActivityMainBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.util.ConnectivityObserver
import uz.texnopos.elektrolife.util.NetworkConnectivityObserver
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var navController: NavController
    private val settings: Settings by inject()
    private var connected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()
        setConnectivityObserver()

        setLocale()
    }

    private fun setLocale() {
        val localeName = settings.language
        val locale = Locale(localeName)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(locale)
        res.updateConfiguration(conf, dm)
    }

    fun rerun() {
        val refresh = Intent(this, MainActivity::class.java)
        refresh.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        this.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        this.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(refresh)
    }

    private fun setConnectivityObserver() {
        connectivityObserver.observe().asLiveData().observe(this) {
            Log.d("CONNECTION", it.name)
            when (it) {
                ConnectivityObserver.Status.Available -> {
                    onConnectionAvailable()
                    connected = true
                }
                ConnectivityObserver.Status.Unavailable -> {
                    if (connected) {
                        navController.navigate(R.id.noInternetFragment)
                    }
                    connected = false
                }
                ConnectivityObserver.Status.Losing -> {
                    Toast.makeText(this, "Losing", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if (connected) {
                        navController.navigate(R.id.noInternetFragment)
                    }
                    connected = false
                }
            }
        }
    }

    private var onConnectionAvailable: () -> Unit = {}
    fun setOnConnectionAvailableListener(onConnectionAvailable: () -> Unit) {
        this.onConnectionAvailable = onConnectionAvailable
    }

    override fun onBackPressed() {
        if (connected) super.onBackPressed()
    }
}
