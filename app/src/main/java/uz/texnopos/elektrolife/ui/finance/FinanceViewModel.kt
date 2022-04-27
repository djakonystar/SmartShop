package uz.texnopos.elektrolife.ui.finance

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class FinanceViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var mutableFinancePost: MutableLiveData<Resource<List<String>>> = MutableLiveData()
    val financePost: LiveData<Resource<List<String>>> = mutableFinancePost

    private var mutableFinanceDetails: MutableLiveData<Resource<List<Finance>>> = MutableLiveData()
    val financeDetails: LiveData<Resource<List<Finance>>> = mutableFinanceDetails

    fun addFinanceDetail(finance: FinancePost) {
        mutableFinancePost.value = Resource.loading()
        compositeDisposable.add(
            api.addFinanceDetail("Bearer ${settings.token}", finance)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableFinancePost.value = Resource.success(response.payload)
                        } else {
                            mutableFinancePost.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableFinancePost.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun getFinanceDetails(from: String, to: String, type: String) {
        mutableFinanceDetails.value = Resource.loading()
        compositeDisposable.add(
            api.getFinanceDetails("Bearer ${settings.token}", from, to, type)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableFinanceDetails.value = Resource.success(response.payload)
                        } else {
                            mutableFinanceDetails.value = Resource.error(response.message)
                        }
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
