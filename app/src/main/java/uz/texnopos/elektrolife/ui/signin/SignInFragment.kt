package uz.texnopos.elektrolife.ui.signin

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.os.*
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import uz.texnopos.elektrolife.R
import uz.texnopos.elektrolife.core.ResourceState
import uz.texnopos.elektrolife.data.model.signin.SignIn
import uz.texnopos.elektrolife.databinding.FragmentSignInBinding
import uz.texnopos.elektrolife.settings.Settings

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var navController: NavController
    private val viewModel: SignInViewModel by viewModel()
    private val settings: Settings by inject()
    private var pincode: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignInBinding.bind(view)
        navController = findNavController()

        binding.apply {
            tvPincode.isVisible = requireActivity().packageName == "uz.texnopos.smartshoptest" ||
                    requireActivity().packageName == "uz.texnopos.smartshopteststore"

            beforeUnlock()

            etPassword.addTextChangedListener {
                if (it.toString().length == 4) {
                    val signIn = SignIn(
                        pincode = it.toString()
                    )
                    pincode = it.toString()
                    viewModel.signIn(signIn = signIn)
                }
            }

            val ic: InputConnection? = etPassword.onCreateInputConnection(EditorInfo())
            keyboard.setInputConnection(ic)
        }

        setUpObservers()
    }

    private fun setLoading(loading: Boolean) {
        binding.apply {
            progressBar.isVisible = loading
            etPassword.isEnabled = !loading
        }
    }

    private fun setUpObservers() {
        viewModel.signIn.observe(viewLifecycleOwner) {
            when (it.status) {
                ResourceState.LOADING -> setLoading(true)
                ResourceState.SUCCESS -> {
                    setLoading(false)
                    if (it.data!!.successful) {
                        val user = it.data.payload
                        login(true, user.id, user.token, "")
                    } else {
                        login(false, 0, "", requireContext().getString(R.string.wrong_pincode))
                    }
                }
                ResourceState.ERROR -> {
                    setLoading(false)
                    login(false, 0, "", it.message!!)
                }
            }
        }
    }

    private fun login(login: Boolean, id: Int, token: String, error: String) {
        if (login) {
            binding.etPassword.setText("")
            binding.etPassword.clearFocus()
            settings.userId = id
            settings.token = token
            afterUnlock()
        } else {
            val snackbar = Snackbar.make(binding.etPassword, error, Snackbar.LENGTH_SHORT)
//            val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
//            params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
//            params.width = FrameLayout.LayoutParams.MATCH_PARENT
//            snackbar.view.layoutParams = params
            snackbar.show()

            val shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake)
            binding.etPassword.startAnimation(shake)
            binding.tvDescription.startAnimation(shake)
            binding.lottieLock.startAnimation(shake)
            binding.etPassword.setText("")
            binding.etPassword.clearFocus()


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
        }
    }

    private fun beforeUnlock() {
        binding.apply {
            lottieLock.setMinAndMaxFrame(0, 60)
            lottieLock.repeatCount = ValueAnimator.INFINITE
            lottieLock.playAnimation()
        }
    }

    private fun afterUnlock() {
        binding.apply {
            lottieLock.setMinAndMaxFrame(60, 95)
            lottieLock.repeatCount = 0
//            lottieLock.speed = 3.0f
            lottieLock.playAnimation()
            lottieLock.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {

                }

                override fun onAnimationEnd(p0: Animator?) {
                    navController.navigate(R.id.action_signInFragment_to_mainFragment)
                }

                override fun onAnimationCancel(p0: Animator?) {

                }

                override fun onAnimationRepeat(p0: Animator?) {

                }
            })
        }
    }
}
