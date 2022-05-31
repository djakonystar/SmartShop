package uz.texnopos.elektrolife

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import uz.texnopos.elektrolife.core.utils.DynamicRetrofit
import uz.texnopos.elektrolife.databinding.ActivityMainBinding
import uz.texnopos.elektrolife.settings.Settings
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val settings: Settings by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
