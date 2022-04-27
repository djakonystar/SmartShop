package uz.texnopos.elektrolife.ui.newsale

import androidx.core.util.Pair
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.newSaleProduct
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import java.util.concurrent.TimeUnit

class NewSaleViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val searchSubject = BehaviorSubject.create<Pair<Int, String>>()
    private var page = 0

    private var mutableProducts: MutableLiveData<Resource<List<newSaleProduct>>> = MutableLiveData()
    val products: LiveData<Resource<List<newSaleProduct>>> = mutableProducts

    private var mutableProduct: MutableLiveData<Resource<newSaleProduct>> = MutableLiveData()
    val product: LiveData<Resource<newSaleProduct>> = mutableProduct

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { pair ->
                if (pair.first == -1) {
                    api.getProducts(
                        token = "Bearer ${settings.token}",
                        page = page,
                        name = pair.second
                    ).subscribeOn(Schedulers.io())
                } else {
                    api.getProducts(
                        token = "Bearer ${settings.token}",
                        page = page,
                        categoryId = pair.first,
                        name = pair.second
                    ).subscribeOn(Schedulers.io())
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableProducts.value = Resource.success(response.payload.data)
                    } else {
                        mutableProducts.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableProducts.value = Resource.error(error.message)
                }
            )
    }

    fun getProducts(page: Int) {
        this.page = page
        mutableProducts.value = Resource.loading()
        compositeDisposable.add(
            api.getProducts(token = "Bearer ${settings.token}", page = this.page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableProducts.value = Resource.success(response.payload.data)
                        } else {
                            mutableProducts.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableProducts.value = Resource.error(error.message)
                    }
                )
        )
    }

    fun getProducts(page: Int, searchValue: String) {
        this.page = page
        mutableProducts.value = Resource.loading()
        searchSubject.onNext(Pair(-1, searchValue))
    }

    fun getProducts(page: Int, categoryId: Int, searchValue: String) {
        this.page = page
        mutableProducts.value = Resource.loading()
        searchSubject.onNext(Pair(categoryId, searchValue))
    }

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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
