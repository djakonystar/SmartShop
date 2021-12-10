package uz.texnopos.electrolightwarehouse.ui.newsale.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.model.Order
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class OrderViewModel(private val api:ApiInterface, private val settings: Settings): ViewModel() {

    private var _orderState:MutableLiveData<Resource<Any>> = MutableLiveData()
    val orderState:LiveData<Resource<Any>> get() = _orderState

    fun setOrder(order: Order){
        _orderState.value = Resource.loading()
        api.order("Bearer ${settings.token}",order)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.successful){
                        _orderState.value = Resource.success(it.payload)
                    }else{
                        _orderState.value = Resource.error(it.message)
                    }
                },{
                    _orderState.value = Resource.error(it.localizedMessage)
                }
            )

    }

}