package uz.texnopos.elektrolife.ui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.payment.AddPayment
import uz.texnopos.elektrolife.data.model.payment.PaymentHistory
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class PaymentViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableAddPayment: MutableLiveData<Resource<Any>> = MutableLiveData()
    val addPayment: LiveData<Resource<Any>> = mutableAddPayment

    private var mutablePayments: MutableLiveData<Resource<PagingResponse<PaymentHistory>>> =
        MutableLiveData()
    val payments: LiveData<Resource<PagingResponse<PaymentHistory>>> = mutablePayments

    fun addPayment(addPayment: AddPayment) {
        mutableAddPayment.value = Resource.loading()
        compositeDisposable.add(
            api.addPayment(token = "Bearer ${settings.token}", addPayment = addPayment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableAddPayment.value = Resource.success(response.payload)
                        } else {
                            mutableAddPayment.value = Resource.error(response.message)
                        }
                    },
                    { throwable ->
                        if (throwable is HttpException) {
                            if (throwable.code() == 401) {
                                mutableAddPayment.value = Resource.error("Unauthorized")
                            }
                        } else {
                            mutableAddPayment.value = Resource.error(throwable.message)
                        }
                    }
                )
        )
    }

    fun getPayments(page: Int, from: String, to: String) {
        mutablePayments.value = Resource.loading()
        compositeDisposable.add(api.getPayments(
            token = "Bearer ${settings.token}",
            page = page,
            from = from,
            to = to
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutablePayments.value = Resource.success(response.payload)
                    } else {
                        mutablePayments.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutablePayments.value = Resource.error(error.message)
                }
            ))
    }

    fun getPayments(page: Int, from: String, to: String, clientId: Int) {
        mutablePayments.value = Resource.loading()
        compositeDisposable.add(
            api.getPayments(
                token = "Bearer ${settings.token}",
                page = page,
                from = from,
                to = to,
                clientId = clientId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutablePayments.value = Resource.success(response.payload)
                        } else {
                            mutablePayments.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutablePayments.value = Resource.error(error.message)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
