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

    private var mutableBalance: MutableLiveData<Resource<GenericResponse<Balance>>> =
        MutableLiveData()
    val balance: LiveData<Resource<GenericResponse<Balance>>> = mutableBalance

    fun getCashboxBalance() {
        mutableBalance.value = Resource.loading()
        compositeDisposable.add(
            api.getCashboxBalance("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableBalance.value = Resource.success(response)
                    },
                    { error ->
                        mutableBalance.value = Resource.error(error.localizedMessage)
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
}
