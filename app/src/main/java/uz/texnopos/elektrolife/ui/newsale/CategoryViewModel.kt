package uz.texnopos.elektrolife.ui.newsale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.newsale.Product
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class CategoryViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {

    private var _categories: MutableLiveData<Resource<List<CategoryResponse>>> = MutableLiveData()
    val categories: LiveData<Resource<List<CategoryResponse>>> get() = _categories

    private val compositeDisposable = CompositeDisposable()

    fun getCategories() {
        _categories.value = Resource.loading()
        compositeDisposable.add(
            api.getCategories("Bearer ${settings.token}")
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
