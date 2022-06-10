package uz.texnopos.elektrolife.ui.newproduct

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MultipartBody
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.newproduct.Image
import uz.texnopos.elektrolife.data.retrofit.ImageApiInterface

class ImageViewModel(private val api: ImageApiInterface): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableImage: MutableLiveData<Resource<Image>> = MutableLiveData()
    val image: LiveData<Resource<Image>> = mutableImage

    fun uploadImage(cloudName: String, image: MultipartBody.Part, preset: MultipartBody.Part) {
        mutableImage.value = Resource.loading()
        compositeDisposable.add(
            api.uploadImage(cloudName, image, preset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableImage.value = Resource.success(response)
                    },
                    { error ->
                        mutableImage.value = Resource.error(error.message)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
