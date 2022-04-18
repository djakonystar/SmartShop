package uz.texnopos.elektrolife.ui.finance.reports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.finance.Cashier
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class ReportsViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableCashbox: MutableLiveData<Resource<GenericResponse<Cashier>>> =
        MutableLiveData()
    val cashbox: LiveData<Resource<GenericResponse<Cashier>>> = mutableCashbox

    fun getCashbox(from: String, to: String) {
        mutableCashbox.postValue(Resource.loading())
        compositeDisposable.add(
            api.getCashier("Bearer ${settings.token}", from = from, to = to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableCashbox.postValue(Resource.success(response))
                        } else {
                            mutableCashbox.postValue(Resource.error(response.message))
                        }
                    },
                    { error ->
                        mutableCashbox.postValue(Resource.error(error.localizedMessage))
                    }
                )
        )
    }

    private var mutableProfit: MutableLiveData<Resource<GenericResponse<Cashier>>> =
        MutableLiveData()
    val profit: LiveData<Resource<GenericResponse<Cashier>>> = mutableProfit

    fun getProfit(from: String, to: String) {
        mutableProfit.value = Resource.loading()
        compositeDisposable.add(
            api.getCashier("Bearer ${settings.token}", from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableProfit.value = Resource.success(response)
                        } else {
                            mutableProfit.postValue(Resource.error(response.message))
                        }
                    },
                    { error ->
                        mutableProfit.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
