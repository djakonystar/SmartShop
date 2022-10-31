package uz.texnopos.elektrolife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.databinding.ActivityMainBinding
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.util.ConnectivityObserver
import uz.texnopos.elektrolife.util.NetworkConnectivityObserver
import java.util.*

class MainActivity : AppCompatActivity(), InstallStateUpdatedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityObserver: ConnectivityObserver
    private lateinit var navController: NavController
    private lateinit var appUpdateManager: AppUpdateManager
    private val settings: Settings by inject()
    private var connected: Boolean = false

    companion object {
        private const val REQUEST_CODE_FLEXIBLE_UPDATE = 117117
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.findNavController()

        appUpdateManager = AppUpdateManagerFactory.create(this)

        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                requestUpdate(it)
            }
        }

        setConnectivityObserver()

        setLocale()
    }

    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUpdate()
            }
        }
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.FLEXIBLE,
            this,
            REQUEST_CODE_FLEXIBLE_UPDATE
        )
    }

    override fun onStateUpdate(p0: InstallState) {
        if (p0.installStatus() == InstallStatus.DOWNLOADED) {
            notifyUpdate()
        }
    }

    private fun notifyUpdate() {
        Snackbar.make(
            binding.root,
            getString(R.string.update_downloaded_msg),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.install)) {
                appUpdateManager.completeUpdate()
                appUpdateManager.unregisterListener(this)
            }
            .setActionTextColor(ContextCompat.getColor(this, R.color.app_main_color))
            .show()
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
