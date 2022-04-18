package uz.texnopos.elektrolife.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.newproduct.Categories
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.data.model.newproduct.ProductId
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewProductViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var mutableCategories: MutableLiveData<Resource<GenericResponse<List<Categories>>>> =
        MutableLiveData()
    val categories: LiveData<Resource<GenericResponse<List<Categories>>>> get() = mutableCategories

    fun getCategories() {
        mutableCategories.value = Resource.loading()
        compositeDisposable.add(
            api.getCategories("Bearer ${settings.token}")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableCategories.value = Resource.success(response)
                    },
                    { error ->
                        mutableCategories.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    private var mutableProduct: MutableLiveData<Resource<GenericResponse<ProductId>>> =
        MutableLiveData()
    val createProduct: LiveData<Resource<GenericResponse<ProductId>>> get() = mutableProduct

    fun createProduct(product: Product) {
        mutableProduct.value = Resource.loading()
        compositeDisposable.add(
            api.createdProduct("Bearer ${settings.token}", product)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableProduct.value = Resource.success(response)
                    },
                    { error ->
                        mutableProduct.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
