package uz.texnopos.elektrolife.ui.currency

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.currency.Currency
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class CurrencyViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableCurrency: MutableLiveData<Resource<List<Currency>>> = MutableLiveData()
    val currency: LiveData<Resource<List<Currency>>> = mutableCurrency

    fun getCurrency() {
        mutableCurrency.value = Resource.loading()
        compositeDisposable.add(
            api.getCurrency("Bearer ${settings.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.isSuccessful) {
                            mutableCurrency.value = Resource.success(response.body()!!.payload)
                        } else {
                            mutableCurrency.value = Resource.error(response.message())
                        }
                    },
                    { error ->
                        mutableCurrency.value = Resource.error(error.message)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
