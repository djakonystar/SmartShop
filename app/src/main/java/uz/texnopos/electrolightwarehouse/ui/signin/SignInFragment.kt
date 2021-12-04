package uz.texnopos.electrolightwarehouse.ui.signin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.os.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import uz.texnopos.electrolightwarehouse.R
import uz.texnopos.electrolightwarehouse.databinding.FragmentSignInBinding

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding: FragmentSignInBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)

        binding.apply {
            etPassword.addTextChangedListener {
                if (it.toString().length == 4) {
                    login(it.toString())
                }
            }
        }
    }

    private fun login(login: String) {
        if (login == "4565") {
            Snackbar.make(binding.etPassword, "Success!", Snackbar.LENGTH_SHORT).show()
            binding.etPassword.setText("")
            binding.etPassword.clearFocus()
        } else {
            Snackbar.make(binding.etPassword, "Error!", Snackbar.LENGTH_SHORT).show()
            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            binding.etPassword.startAnimation(shake)
            Handler(Looper.getMainLooper()).postDelayed(100) {
                kotlin.run {
                    binding.etPassword.setText("")
                    binding.etPassword.clearFocus()
                }
            }
        }
    }
}