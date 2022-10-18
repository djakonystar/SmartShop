package uz.texnopos.elektrolife.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class ClientViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject = BehaviorSubject.create<String>()

    private var mutableClients: MutableLiveData<Resource<GenericResponse<List<Client>>>> =
        MutableLiveData()
    val clients: LiveData<Resource<GenericResponse<List<Client>>>> = mutableClients

    private var mutableSearchClient: MutableLiveData<Resource<GenericResponse<List<Client>>>> =
        MutableLiveData()
    val searchClient: LiveData<Resource<GenericResponse<List<Client>>>> get() = mutableSearchClient

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap {
                api.getClientsByName("Bearer ${settings.token}", it)
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

    fun getClients(limit: Int, page: Int, search: String) {
        mutableClients.value = Resource.loading()
        compositeDisposable.add(
            api.getClients("Bearer ${settings.token}", limit, page, search)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableClients.value = Resource.success(response)
                    },
                    { error ->
                        mutableClients.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun searchClient(search: String) {
        mutableSearchClient.value = Resource.loading()
        searchSubject.onNext(search)
    }

}
