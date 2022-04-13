package uz.texnopos.elektrolife.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.currency.Currency
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class MainViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableDollarRate: MutableLiveData<Resource<GenericResponse<List<Currency>>>> =
        MutableLiveData()
    val dollarRate: LiveData<Resource<GenericResponse<List<Currency>>>> = mutableDollarRate

    fun getCurrency() {
        mutableDollarRate.value = Resource.loading()
        compositeDisposable.add(
            api.getCurrency("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.isSuccessful) {
                            mutableDollarRate.value = Resource.success(response.body()!!)
                        } else {
                            mutableDollarRate.postValue(Resource.error(response.message()))
                        }
                    },
                    { error ->
                        mutableDollarRate.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
