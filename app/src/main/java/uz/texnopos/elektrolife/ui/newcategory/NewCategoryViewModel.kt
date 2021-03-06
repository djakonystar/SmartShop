package uz.texnopos.elektrolife.ui.newcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.newcategory.CategoryId
import uz.texnopos.elektrolife.data.model.newcategory.NewCategory
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewCategoryViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private var _newCategory: MutableLiveData<Resource<GenericResponse<CategoryId>>> =
        MutableLiveData()
    val newCategory: LiveData<Resource<GenericResponse<CategoryId>>> get() = _newCategory

    fun createdNewCategory(newCategory: NewCategory) {
        _newCategory.value = Resource.loading()
        compositeDisposable.add(api.createdCategory("Bearer ${settings.token}", newCategory)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.successful) {
                        _newCategory.value = Resource.success(it)
                    } else {
                        _newCategory.value = Resource.error(it.message)
                    }
                },
                {
                    _newCategory.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }
}