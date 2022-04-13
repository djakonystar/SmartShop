package uz.texnopos.elektrolife.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.signin.DollarRate
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class MainViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableDollarRate: MutableLiveData<Resource<GenericResponse<DollarRate>>> =
        MutableLiveData()
    val dollarRate: LiveData<Resource<GenericResponse<DollarRate>>> = mutableDollarRate

    fun getDollarRate() {
        mutableDollarRate.value = Resource.loading()
        compositeDisposable.add(
            api.getDollarRate("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableDollarRate.value = Resource.success(response)
                    },
                    { error ->
                        mutableDollarRate.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
