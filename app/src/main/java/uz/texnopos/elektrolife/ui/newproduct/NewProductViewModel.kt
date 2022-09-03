package uz.texnopos.elektrolife.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.createdProduct
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.data.model.warehouse_item.Data
import uz.texnopos.elektrolife.data.model.warehouse_item.Payload
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class NewProductViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private var searchSubject = BehaviorSubject.create<String>()

    private var mutableProduct: MutableLiveData<Resource<createdProduct>> = MutableLiveData()
    val product: LiveData<Resource<createdProduct>> = mutableProduct

    private var mutableWarehouseProducts: MutableLiveData<Resource<List<Data>>> =
        MutableLiveData()
    val warehouseProducts: LiveData<Resource<List<Data>>> = mutableWarehouseProducts

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { value ->
                api.getCurrentProduct(token = "Bearer ${settings.token}", search = value)
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableWarehouseProducts.value = Resource.success(response.payload.data)
                    } else {
                        mutableWarehouseProducts.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableWarehouseProducts.value = Resource.error(error.message)
                }
            )
    }

    fun getWarehouseProducts(name: String) {
        mutableWarehouseProducts.value = Resource.loading()
        searchSubject.onNext(name)
    }

    fun createProduct(product: Product) {
        mutableProduct.value = Resource.loading()
        compositeDisposable.add(
            api.createProduct("Bearer ${settings.token}", product)
                .subscribeOn(Schedulers.newThread())
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
                        mutableProduct.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
