package uz.texnopos.elektrolife.ui.finance.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.finance.Balance
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class ReportsViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableCashboxBalance: MutableLiveData<Resource<GenericResponse<Balance>>> =
        MutableLiveData()
    val cashboxBalance: LiveData<Resource<GenericResponse<Balance>>> = mutableCashboxBalance

    private var mutableProfit: MutableLiveData<Resource<GenericResponse<Balance>>> =
        MutableLiveData()
    val profit: LiveData<Resource<GenericResponse<Balance>>> = mutableProfit

    fun getCashboxBalance(from: String, to: String) {
        mutableCashboxBalance.value = Resource.loading()
        compositeDisposable.add(
            api.getCashboxBalance("Bearer ${settings.token}", from, to)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableCashboxBalance.value = Resource.success(response)
                    },
                    { error ->
                        mutableCashboxBalance.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun getProfit(from: String, to: String) {
        mutableProfit.value = Resource.loading()
        compositeDisposable.add(
            api.getProfit("Bearer ${settings.token}", from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableProfit.value = Resource.success(response)
                    },
                    { error ->
                        mutableProfit.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
