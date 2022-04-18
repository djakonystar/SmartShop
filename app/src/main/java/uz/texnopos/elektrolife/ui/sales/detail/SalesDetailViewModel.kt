package uz.texnopos.elektrolife.ui.sales.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.sales.Order
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class SalesDetailViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableOrders: MutableLiveData<Resource<List<Order>>> = MutableLiveData()
    val orders: LiveData<Resource<List<Order>>> = mutableOrders

    fun getOrders(basketId: Int) {
        mutableOrders.value = Resource.loading()
        compositeDisposable.add(
            api.getOrders("Bearer ${settings.token}", basketId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableOrders.value = Resource.success(response.payload)
                        } else {
                            mutableOrders.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableOrders.value = Resource.error(error.message)
                    }
                )
        )
    }
}
