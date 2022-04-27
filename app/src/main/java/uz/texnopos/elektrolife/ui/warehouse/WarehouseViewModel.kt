package uz.texnopos.elektrolife.ui.warehouse

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.warehouse.Product
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class WarehouseViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val searchSubject = BehaviorSubject.create<Pair<Int, String>>()
    private var page = 0

    private var mutableWarehouseProducts: MutableLiveData<Resource<PagingResponse<List<WarehouseItem>>>> =
        MutableLiveData()
    val warehouseProducts: LiveData<Resource<PagingResponse<List<WarehouseItem>>>> =
        mutableWarehouseProducts

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { pair ->
                if (pair.first == -1) {
                    api.warehouseProducts(
                        token = "Bearer ${settings.token}",
                        page = page,
                        search = pair.second
                    ).subscribeOn(Schedulers.io())
                } else {
                    api.warehouseProducts(
                        token = "Bearer ${settings.token}",
                        page = page,
                        categoryId = pair.first,
                        search = pair.second
                    ).subscribeOn(Schedulers.io())
                }
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

    fun warehouseProducts(page: Int) {
        this.page = page
        mutableWarehouseProducts.value = Resource.loading()
        compositeDisposable.add(
            api.warehouseProducts(token = "Bearer ${settings.token}", page = this.page)
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

    fun warehouseProducts(page: Int, name: String) {
        this.page = page
        mutableWarehouseProducts.value = Resource.loading()
        searchSubject.onNext(Pair(-1, name))
    }

    fun warehouseProducts(page: Int, categoryId: Int, name: String) {
        this.page = page
        mutableWarehouseProducts.value = Resource.loading()
        searchSubject.onNext(Pair(categoryId, name))
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
