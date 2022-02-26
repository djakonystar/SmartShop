package uz.texnopos.elektrolife.ui.client.detail.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.sales.Sales
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class ClientSalesViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableClientSales: MutableLiveData<Resource<GenericResponse<List<Sales>>>> =
        MutableLiveData()
    val clientSales: LiveData<Resource<GenericResponse<List<Sales>>>> = mutableClientSales

    fun getClientSales(clientId: Int) {
        mutableClientSales.value = Resource.loading()
        compositeDisposable.add(
            api.getSalesOfClient("Bearer ${settings.token}", clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableClientSales.value = Resource.success(response)
                    },
                    { error ->
                        mutableClientSales.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
