package uz.texnopos.electrolightwarehouse.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class SalesViewModel(private val api:ApiInterface, private val settings: Settings): ViewModel() {
    private var _orders:MutableLiveData<Resource<List<Sales>>> = MutableLiveData()
    val orders:LiveData<Resource<List<Sales>>> get() = _orders

    private val compositeDisposable = CompositeDisposable()
    fun getOrders() {
        _orders.value = Resource.loading()
        compositeDisposable.add(
            api.getOrders("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.successful) {
                            _orders.value = Resource.success(it.payload)
                        } else {
                            _orders.value = Resource.error(it.message)
                        }
                    }, {
                        _orders.value = Resource.error(it.localizedMessage)
                    }
                )
        )
    }
    fun getOrdersByDate(from:String, to:String){
        _orders.value = Resource.loading()
        compositeDisposable.add(
            api.getOrdersByDate("Bearer ${settings.token}",from,to)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.successful) {
                            _orders.value = Resource.success(it.payload)
                        } else {
                            _orders.value = Resource.error(it.message)
                        }
                    }, {
                        _orders.value = Resource.error(it.localizedMessage)
                    }
                )
        )
    }
}