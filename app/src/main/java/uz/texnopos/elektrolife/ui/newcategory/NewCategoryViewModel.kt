package uz.texnopos.elektrolife.ui.newcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.newCategory
import uz.texnopos.elektrolife.data.model.newcategory.CategoryPost
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewCategoryViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var _newCategory: MutableLiveData<Resource<newCategory>> = MutableLiveData()
    val newCategory: LiveData<Resource<newCategory>> = _newCategory

    fun createCategory(categoryPost: CategoryPost) {
        _newCategory.value = Resource.loading()
        compositeDisposable.add(
            api.createCategory("Bearer ${settings.token}", categoryPost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            _newCategory.value = Resource.success(response.payload)
                        } else {
                            _newCategory.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        _newCategory.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
