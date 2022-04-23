package uz.texnopos.elektrolife.ui.newclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.newclient.ClientId
import uz.texnopos.elektrolife.data.model.newclient.Client
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewClientViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var mutableRegister: MutableLiveData<Resource<ClientId>> = MutableLiveData()
    val registerNewClient: LiveData<Resource<ClientId>> = mutableRegister

    fun registerNewClient(client: Client) {
        mutableRegister.value = Resource.loading()
        compositeDisposable.add(
            api.addNewClient("Bearer ${settings.token}", client)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableRegister.value = Resource.success(response.payload)
                        } else {
                            mutableRegister.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableRegister.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
