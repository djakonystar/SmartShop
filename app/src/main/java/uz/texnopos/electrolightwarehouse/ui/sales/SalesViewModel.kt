package uz.texnopos.electrolightwarehouse.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface

class SalesViewModel(): ViewModel() {
    private var _orders:MutableLiveData<Resource<List<Sales>>> = MutableLiveData()
    val orders:LiveData<Resource<List<Sales>>> get() = _orders

    private val compositeDisposable = CompositeDisposable()
//    fun getOrders() {
//        _orders.value = Resource.loading()
//        compositeDisposable.add(
//            api.getOrders("Bearer 1|AfRBO8BbtRQnZfjYR0MOH4zlpa9KKe597QR5PQTk").subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        if (it.successful) {
//                            _orders.value = Resource.success(it.payload)
//                        } else {
//                            _orders.value = Resource.error(it.message)
//                        }
//                    }, {
//                        _orders.value = Resource.error(it.localizedMessage)
//                    }
//                )
//        )
    }


 //   }