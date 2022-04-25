package uz.texnopos.elektrolife.ui.newsale.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.orderBasketResponse
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class OrderViewModel(private val api:ApiInterface, private val settings: Settings): ViewModel() {

    private var _orderState:MutableLiveData<Resource<orderBasketResponse>> = MutableLiveData()
    val orderState:LiveData<Resource<orderBasketResponse>> get() = _orderState

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