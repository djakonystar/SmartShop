package uz.texnopos.elektrolife.ui.warehouse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.warehouse.EditProduct
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class EditProductViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableEditProduct: MutableLiveData<Resource<Any>> = MutableLiveData()
    val editProduct: LiveData<Resource<Any>> = mutableEditProduct

    fun editProduct(product: EditProduct) {
        mutableEditProduct.value = Resource.loading()
        compositeDisposable.add(
            api.editProduct("Bearer ${settings.token}", product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableEditProduct.value = Resource.success(response.payload)
                        } else {
                            mutableEditProduct.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableEditProduct.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
