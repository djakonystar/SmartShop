package uz.texnopos.electrolightwarehouse.ui.newcategory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.newCategory.CategoryId
import uz.texnopos.electrolightwarehouse.data.newCategory.NewCategory
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface

class NewCategoryViewModel(private val api: ApiInterface): ViewModel() {
    private var compositeDisposable = CompositeDisposable()
    private var _newCategory: MutableLiveData<Resource<GenericResponse<CategoryId>>> = MutableLiveData()
    val newCategory: LiveData<Resource<GenericResponse<CategoryId>>> get() = _newCategory

    fun createdNewCategory(newCategory: NewCategory){
        _newCategory.value = Resource.loading()
        compositeDisposable.add(api.createdCategory("Bearer 5|Cmn3wbVIPlspPYUFvXG9JhCKWCKfMdffyijCvAC3", newCategory)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                if (it.successful)
                    {
                        _newCategory.value = Resource.success(it)
                    }
                    else
                    {
                        _newCategory.value = Resource.error(it.message)
                    }
                }
                ,
                {
                _newCategory.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }
}