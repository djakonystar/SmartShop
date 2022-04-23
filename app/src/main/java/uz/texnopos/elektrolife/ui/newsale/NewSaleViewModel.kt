package uz.texnopos.elektrolife.ui.newsale

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
    private val searchSubject = BehaviorSubject.create<String>()

    private var mutableProducts: MutableLiveData<Resource<List<newSaleProduct>>> = MutableLiveData()
    val products: LiveData<Resource<List<newSaleProduct>>> = mutableProducts

    init {
        searchSubject
            .debounce(700, TimeUnit.MILLISECONDS)
            .switchMap { searchValue ->
                api.getProducts("Bearer ${settings.token}", searchValue)
                    .subscribeOn(Schedulers.io())
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { response ->
                    if (response.successful) {
                        mutableProducts.value = Resource.success(response.payload)
                    } else {
                        mutableProducts.value = Resource.error(response.message)
                    }
                },
                { error ->
                    mutableProducts.value = Resource.error(error.message)
                }
            )
    }

    fun getProducts() {
        mutableProducts.value = Resource.loading()
        compositeDisposable.add(
            api.getProducts("Bearer ${settings.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableProducts.value = Resource.success(response.payload)
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

    fun getProducts(searchValue: String) {
        mutableProducts.value = Resource.loading()
        searchSubject.onNext(searchValue)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
