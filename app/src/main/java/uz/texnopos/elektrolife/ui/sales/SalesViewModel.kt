package uz.texnopos.elektrolife.ui.sales

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

class SalesViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableOrders: MutableLiveData<Resource<GenericResponse<List<Sales>>>> =
        MutableLiveData()
    val orders: LiveData<Resource<GenericResponse<List<Sales>>>> get() = mutableOrders

    fun getOrders() {
        mutableOrders.value = Resource.loading()
        compositeDisposable.add(
            api.getOrders("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableOrders.value = Resource.success(response)
                    },
                    { error ->
                        mutableOrders.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun getOrdersByDate(from: String, to: String) {
        mutableOrders.value = Resource.loading()
        compositeDisposable.add(
            api.getOrdersByDate("Bearer ${settings.token}", from, to)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableOrders.value = Resource.success(response)
                    },
                    { error ->
                        mutableOrders.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}