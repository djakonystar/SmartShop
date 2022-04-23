package uz.texnopos.elektrolife.ui.newcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.core.extensions.newCategory
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.newcategory.CategoryId
import uz.texnopos.elektrolife.data.model.newcategory.CategoryPost
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class NewCategoryViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private var _newCategory: MutableLiveData<Resource<GenericResponse<newCategory>>> =
        MutableLiveData()
    val newCategory: LiveData<Resource<GenericResponse<newCategory>>> get() = _newCategory

    fun createdNewCategory(categoryPost: CategoryPost) {
        _newCategory.value = Resource.loading()
        compositeDisposable.add(api.createCategory("Bearer ${settings.token}", categoryPost)
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