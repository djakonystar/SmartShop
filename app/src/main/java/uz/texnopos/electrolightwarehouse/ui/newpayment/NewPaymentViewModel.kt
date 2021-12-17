package uz.texnopos.electrolightwarehouse.ui.newpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.model.clients.ClientInfo
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.newpayment.NewPayment
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class NewPaymentViewModel(private val api: ApiInterface,private val settings: Settings): ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var mutableNewPayment: MutableLiveData<Resource<GenericResponse<List<String>>>> = MutableLiveData()
    val newPayment: LiveData<Resource<GenericResponse<List<String>>>> get() = mutableNewPayment

    private var mutableSearchClient: MutableLiveData<Resource<GenericResponse<List<ClientInfo>>>> = MutableLiveData()
    val searchClient: LiveData<Resource<GenericResponse<List<ClientInfo>>>> get() = mutableSearchClient

    fun newPayment(newPayment: NewPayment){
        mutableNewPayment.value = Resource.loading()
        compositeDisposable.add(api.payment("Bearer ${settings.token}",newPayment)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                if (it.successful)
                    {
                    mutableNewPayment.value = Resource.success(it)
                    }
                    else
                    {
                    mutableNewPayment.value = Resource.error(it.message)
                    }
                }
                ,
                {
                    mutableNewPayment.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }

    fun searchClient(search: String){
        mutableSearchClient.value = Resource.loading()
        compositeDisposable.add(api.getClients("Bearer ${settings.token}", search)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                if (it.successful){mutableSearchClient.value = Resource.success(it)}else{mutableSearchClient.value = Resource.error(it.message)}
                }
                ,
                {
                   mutableSearchClient.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }
}