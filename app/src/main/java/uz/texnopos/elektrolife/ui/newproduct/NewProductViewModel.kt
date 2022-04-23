package uz.texnopos.elektrolife.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.createdProduct
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewProductViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var mutableProduct: MutableLiveData<Resource<createdProduct>> = MutableLiveData()
    val product: LiveData<Resource<createdProduct>> = mutableProduct

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
}
