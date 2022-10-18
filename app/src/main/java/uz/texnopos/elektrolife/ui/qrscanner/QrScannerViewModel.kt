package uz.texnopos.elektrolife.ui.qrscanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.newSaleProduct
import uz.texnopos.elektrolife.data.model.sales.OrderResponse
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class QrScannerViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableProduct: MutableLiveData<Resource<newSaleProduct>> = MutableLiveData()
    val product: LiveData<Resource<newSaleProduct>> = mutableProduct

    private var mutableOrder: MutableLiveData<Resource<OrderResponse>> = MutableLiveData()
    val order: LiveData<Resource<OrderResponse>> = mutableOrder

    fun getProduct(type: String, uuid: String) {
        mutableProduct.value = Resource.loading()
        compositeDisposable.add(
            api.getProduct("Bearer ${settings.token}", type, uuid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableProduct.value = Resource.success(response.payload)
                        } else {
                            mutableProduct.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableProduct.value = Resource.error(error.message)
                    }
                )
        )
    }

    fun getBasket(type: String, uuid: String) {
        mutableOrder.value = Resource.loading()
        api.getOrders("Bearer ${settings.token}", uuid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableOrder.value = Resource.success(response.payload)
                    } else {
                        mutableOrder.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableOrder.value = Resource.error(error.message)
                }
            )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
