package uz.texnopos.electrolightwarehouse.ui.newsale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.model.CatalogCategory
import uz.texnopos.electrolightwarehouse.data.model.Product
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface

class CategoriesViewModel(private val api:ApiInterface): ViewModel() {

    private var _categories: MutableLiveData<Resource<List<CatalogCategory>>> = MutableLiveData()
    val categories: LiveData<Resource<List<CatalogCategory>>> get() = _categories

    private var _products:MutableLiveData<Resource<List<Product>>> = MutableLiveData()
    val products:LiveData<Resource<List<Product>>> get() = _products

    private val compositeDisposable = CompositeDisposable()
    fun getCategories() {
//        _categories.value = Resource.loading()
//        compositeDisposable.add(
//            api.getCategories("Bearer 1|AfRBO8BbtRQnZfjYR0MOH4zlpa9KKe597QR5PQTk").subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        if (it.successful) {
//                            _categories.value = Resource.success(it.payload)
//                        } else {
//                            _categories.value = Resource.error(it.message)
//                        }
//                    }, {
//                        _categories.value = Resource.error(it.localizedMessage)
//                    }
//                )
//        )
    }

    fun getCategoryById(id: Int){
//        _products.value = Resource.loading()
//        compositeDisposable.add(
//            api.getCategoriesById("Bearer 1|AfRBO8BbtRQnZfjYR0MOH4zlpa9KKe597QR5PQTk",id).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    {
//                        if (it.successful){
//                            _products.value = Resource.success(it.payload)
//                        }else{
//                            _products.value = Resource.error(it.message)
//                        }
//                    },{
//                        _products.value = Resource.error(it.localizedMessage)
//                    }
//                )
//        )
    }
}