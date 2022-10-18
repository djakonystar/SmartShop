package uz.texnopos.elektrolife.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.model.clients.ClientResponse
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class ClientViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val searchSubject = BehaviorSubject.create<String>()
    private var page = 0

    private var mutableClients: MutableLiveData<Resource<PagingResponse<ClientResponse>>> =
        MutableLiveData()
    val clients: LiveData<Resource<PagingResponse<ClientResponse>>> = mutableClients

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { value ->
                api.getClients(token = "Bearer ${settings.token}", page = page, search = value)
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

    fun getClients(page: Int, search: String) {
        this.page = page
        mutableClients.value = Resource.loading()
        searchSubject.onNext(search)
    }
}
