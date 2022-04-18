package uz.texnopos.elektrolife.ui.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.warehouse.Product
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class WarehouseViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val searchSubject = BehaviorSubject.create<String>()

    private var mutableWarehouseProducts: MutableLiveData<Resource<List<WarehouseItem>>> =
        MutableLiveData()
    val warehouseProducts: LiveData<Resource<List<WarehouseItem>>> = mutableWarehouseProducts

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap {
                api.warehouseProducts("Bearer ${settings.token}", it)
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableWarehouseProducts.value = Resource.success(response.payload)
                    } else {
                        mutableWarehouseProducts.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableWarehouseProducts.value = Resource.error(error.localizedMessage)
                }
            )
    }

    fun warehouseProducts() {
        mutableWarehouseProducts.value = Resource.loading()
        compositeDisposable.add(
            api.warehouseProducts("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableWarehouseProducts.value = Resource.success(response.payload)
                        } else {
                            mutableWarehouseProducts.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableWarehouseProducts.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    fun warehouseProducts(name: String) {
        mutableWarehouseProducts.value = Resource.loading()
        searchSubject.onNext(name)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
