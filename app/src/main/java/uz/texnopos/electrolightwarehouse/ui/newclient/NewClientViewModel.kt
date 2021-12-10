package uz.texnopos.electrolightwarehouse.ui.newclient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.newClient.ClientId
import uz.texnopos.electrolightwarehouse.data.newClient.RegisterClient
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class NewClientViewModel(private val api:ApiInterface, private val settings: Settings): ViewModel() {
    private var compositeDisposable =  CompositeDisposable()

    private var mutableRegister: MutableLiveData<Resource<GenericResponse<ClientId>>> = MutableLiveData()
    val registerNewClient: LiveData<Resource<GenericResponse<ClientId>>> get() = mutableRegister

    fun registerNewClient(registerClient: RegisterClient){
        mutableRegister.value = Resource.loading()
        compositeDisposable.add(api.registerNewClient("Bearer ${settings.token}",registerClient)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                if (it.successful)
                    {
                    mutableRegister.value = Resource.success(it)
                    }
                else
                    {
                        mutableRegister.value = Resource.error(it.message)
                    }
                }
                ,
                {
                    mutableRegister.value = Resource.error(it.localizedMessage)
                }
            )
        )

    }
}