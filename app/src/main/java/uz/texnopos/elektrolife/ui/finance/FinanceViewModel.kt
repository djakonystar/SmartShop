package uz.texnopos.elektrolife.ui.finance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.finance.Balance
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class FinanceViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var mutableCashboxBalance: MutableLiveData<Resource<GenericResponse<Balance>>> =
        MutableLiveData()
    val cashboxBalance: LiveData<Resource<GenericResponse<Balance>>> = mutableCashboxBalance

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

    private var mutableProfit: MutableLiveData<Resource<GenericResponse<Balance>>> =
        MutableLiveData()
    val profit: LiveData<Resource<GenericResponse<Balance>>> = mutableProfit

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

    private var mutableFinancePost: MutableLiveData<Resource<GenericResponse<List<String>>>> =
        MutableLiveData()
    val financePost: LiveData<Resource<GenericResponse<List<String>>>> = mutableFinancePost

    fun addFinanceDetail(finance: FinancePost) {
        mutableFinancePost.value = Resource.loading()
        compositeDisposable.add(
            api.addFinanceDetail(
                "Bearer ${settings.token}", finance
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableFinancePost.value = Resource.success(response)
                    },
                    { error ->
                        mutableFinancePost.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    private var mutableFinanceDetails: MutableLiveData<Resource<GenericResponse<List<Finance>>>> =
        MutableLiveData()
    val financeDetails: LiveData<Resource<GenericResponse<List<Finance>>>> = mutableFinanceDetails

    fun getFinanceDetails(from: String, to: String, type: String) {
        mutableFinanceDetails.value = Resource.loading()
        compositeDisposable.add(
            api.getFinanceDetails(
                "Bearer ${settings.token}", from, to, type
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableFinanceDetails.value = Resource.success(response)
                    },
                    { error ->
                        mutableFinanceDetails.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
