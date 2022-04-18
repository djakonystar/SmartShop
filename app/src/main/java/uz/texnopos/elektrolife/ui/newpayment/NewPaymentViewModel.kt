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
import uz.texnopos.elektrolife.data.model.clients.Client
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
                api.getClients("Bearer ${settings.token}", it)
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    mutableSearchClient.value = Resource.success(response)
                },
                { error ->
                    mutableSearchClient.value = Resource.error(error.localizedMessage)
                }
            )
    }

    private var mutableNewPayment: MutableLiveData<Resource<GenericResponse<List<String>>>> =
        MutableLiveData()
    val newPayment: LiveData<Resource<GenericResponse<List<String>>>> get() = mutableNewPayment

    private var mutableSearchClient: MutableLiveData<Resource<GenericResponse<List<Client>>>> =
        MutableLiveData()
    val searchClient: LiveData<Resource<GenericResponse<List<Client>>>> get() = mutableSearchClient

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
        mutableSearchClient.value = Resource.loading()
        searchSubject.onNext(search)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}