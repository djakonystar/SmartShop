package uz.texnopos.electrolightwarehouse.ui.signin

import android.content.Context
import android.os.*
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.os.postDelayed
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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
            findNavController().navigate(R.id.action_signInFragment_to_mainFragment)
        } else {
            val snackbar = Snackbar.make(binding.etPassword, "Error!", Snackbar.LENGTH_SHORT)
            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            params.width = FrameLayout.LayoutParams.MATCH_PARENT
            snackbar.view.layoutParams = params
            snackbar.show()

            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            binding.etPassword.startAnimation(shake)

            val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        200, VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(200)
            }

            Handler(Looper.getMainLooper()).postDelayed(100) {
                kotlin.run {
                    binding.etPassword.setText("")
                    binding.etPassword.clearFocus()
                }
            }
        }
    }
}