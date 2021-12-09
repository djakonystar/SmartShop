package uz.texnopos.electrolightwarehouse.ui.client

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class ClientsViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableClients: MutableLiveData<Resource<GenericResponse<List<Client>>>> =
        MutableLiveData()
    val clients: LiveData<Resource<GenericResponse<List<Client>>>> = mutableClients

    fun getClients() {
        mutableClients.value = Resource.loading()
        compositeDisposable.add(
            api.getClients("Bearer ${settings.token}")
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
}
