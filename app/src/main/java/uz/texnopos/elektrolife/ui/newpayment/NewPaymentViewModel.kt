package uz.texnopos.elektrolife.ui.newpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.clients.ClientResponse
import uz.texnopos.elektrolife.data.model.newpayment.NewPayment
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class NewPaymentViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private var searchSubject = BehaviorSubject.create<String>()

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap {
                api.getClients(token = "Bearer ${settings.token}", search = it)
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableClients.value = Resource.success(response.payload)
                    } else {
                        mutableClients.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableClients.value = Resource.error(error.message)
                }
            )
    }

    private var mutableNewPayment: MutableLiveData<Resource<GenericResponse<List<String>>>> =
        MutableLiveData()
    val newPayment: LiveData<Resource<GenericResponse<List<String>>>> get() = mutableNewPayment

    private var mutableClients: MutableLiveData<Resource<PagingResponse<ClientResponse>>> =
        MutableLiveData()
    val clients: LiveData<Resource<PagingResponse<ClientResponse>>> = mutableClients

    fun newPayment(newPayment: NewPayment) {
        mutableNewPayment.value = Resource.loading()
        compositeDisposable.add(api.payment("Bearer ${settings.token}", newPayment)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.successful) {
                        mutableNewPayment.value = Resource.success(it)
                    } else {
                        mutableNewPayment.value = Resource.error(it.message)
                    }
                },
                {
                    mutableNewPayment.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }

    fun searchClient(search: String) {
        mutableClients.value = Resource.loading()
        searchSubject.onNext(search)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}