package uz.texnopos.elektrolife.ui.client.detail.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.clients.ClientPayment
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class ClientPaymentViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableClientPayments: MutableLiveData<Resource<GenericResponse<List<ClientPayment>>>> =
        MutableLiveData()
    val clientPayments: LiveData<Resource<GenericResponse<List<ClientPayment>>>> =
        mutableClientPayments

    fun getClientPayments(clientId: Int) {
        mutableClientPayments.value = Resource.loading()
        compositeDisposable.add(
            api.getClientPayments("Bearer ${settings.token}", clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableClientPayments.value = Resource.success(response)
                    },
                    { error ->
                        mutableClientPayments.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
