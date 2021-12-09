package uz.texnopos.electrolightwarehouse.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.newProduct.Categories
import uz.texnopos.electrolightwarehouse.data.newProduct.Product
import uz.texnopos.electrolightwarehouse.data.newProduct.ProductId
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings

class NewProductViewModel(private val api: ApiInterface,private val settings: Settings): ViewModel(){
    private var compositeDisposable = CompositeDisposable()

    private var mutableCategories: MutableLiveData<Resource<GenericResponse<List<Categories>>>> = MutableLiveData()
    val categories: LiveData<Resource<GenericResponse<List<Categories>>>> get() = mutableCategories

    private var mutableProduct: MutableLiveData<Resource<GenericResponse<ProductId>>> = MutableLiveData()
    val createProduct: LiveData<Resource<GenericResponse<ProductId>>> get() = mutableProduct

    fun getCategories(){
        mutableCategories.value = Resource.loading()
        compositeDisposable.add(api.getCategories("Bearer ${settings.token}")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.successful){
                        mutableCategories.value = Resource.success(it)
                    }else{
                        mutableCategories.value = Resource.error(it.message)
                    }
                }
                ,
                {
                    mutableCategories.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }

    fun createProduct(product: Product){
        mutableProduct.value = Resource.loading()
        compositeDisposable.add(api.createdProduct("Bearer ${settings.token}", product)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.successful){
                        mutableProduct.value = Resource.success(it)
                    }else{
                        mutableProduct.value = Resource.error(it.message)
                    }
                }
                ,
                {
                    mutableProduct.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }

}