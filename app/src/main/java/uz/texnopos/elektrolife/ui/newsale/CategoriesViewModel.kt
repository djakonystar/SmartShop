package uz.texnopos.elektrolife.ui.newsale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.newsale.CatalogCategory
import uz.texnopos.elektrolife.data.model.newsale.Products
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class CategoriesViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private var _categories: MutableLiveData<Resource<List<CatalogCategory>>> = MutableLiveData()
    val categories: LiveData<Resource<List<CatalogCategory>>> get() = _categories

    private val compositeDisposable = CompositeDisposable()

    fun getCategories() {
        _categories.value = Resource.loading()
        compositeDisposable.add(
            api.getCatalogCategories("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.successful) {
                            _categories.value = Resource.success(it.payload)
                        } else {
                            _categories.value = Resource.error(it.message)
                        }
                    }, {
                        _categories.value = Resource.error(it.localizedMessage)
                    }
                )
        )
    }

    private var _products: MutableLiveData<Resource<Products>> = MutableLiveData()
    val products: LiveData<Resource<Products>> get() = _products

    fun getProductsByCategoryId(categoryId: Int) {
        _products.value = Resource.loading()
        compositeDisposable.add(
            api.getProductsByCategoryId("Bearer ${settings.token}", categoryId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.successful) {
                            _products.value = Resource.success(it.payload)
                        } else {
                            _products.value = Resource.error(it.message)
                        }
                    }, {
                        _products.value = Resource.error(it.localizedMessage)
                    }
                )
        )
    }

    fun getProductByName(name: String) {
        _products.value = Resource.loading()
        compositeDisposable.add(
            api.getProduct("Bearer ${settings.token}", name, limit = 1000)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if (it.successful) {
                            _products.value = Resource.success(it.payload)
                        } else {
                            _products.value = Resource.error(it.message)
                        }
                    }, {
                        _products.value = Resource.error(it.localizedMessage)
                    }
                )
        )
    }
}