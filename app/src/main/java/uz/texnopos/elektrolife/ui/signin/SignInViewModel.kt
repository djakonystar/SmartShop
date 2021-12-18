package uz.texnopos.elektrolife.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.signin.SignInPayload
import uz.texnopos.elektrolife.data.model.signin.SignInPost
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class SignInViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableSignIn: MutableLiveData<Resource<GenericResponse<SignInPayload>>> =
        MutableLiveData()
    val signIn: LiveData<Resource<GenericResponse<SignInPayload>>> = mutableSignIn

    fun signIn(signIn: SignInPost) {
        mutableSignIn.value = Resource.loading()
        compositeDisposable.add(
            api.signIn(signInPost = signIn)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful){
                            mutableSignIn.value = Resource.success(response)
                            settings.role = response.payload.role
                        }else{
                            mutableSignIn.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableSignIn.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
