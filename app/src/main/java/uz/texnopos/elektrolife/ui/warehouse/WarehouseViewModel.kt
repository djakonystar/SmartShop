package uz.texnopos.elektrolife.ui.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.warehouse.Product
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class WarehouseViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableWarehouseProducts: MutableLiveData<Resource<GenericResponse<List<Product>>>> =
        MutableLiveData()
    val warehouseProducts: LiveData<Resource<GenericResponse<List<Product>>>> =
        mutableWarehouseProducts

    fun getProductsFromWarehouse() {
        mutableWarehouseProducts.value = Resource.loading()
        compositeDisposable.add(
            api.getProductsFromWarehouse("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableWarehouseProducts.value = Resource.success(response)
                    },
                    { error ->
                        mutableWarehouseProducts.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun getProductsByName(name: String) {
        mutableWarehouseProducts.value = Resource.loading()
        compositeDisposable.add(
            api.getProductsFromWarehouse(
                "Bearer ${settings.token}", name
            )
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableWarehouseProducts.value = Resource.success(response)
                    },
                    { error ->
                        mutableWarehouseProducts.value =
                            Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}