package uz.texnopos.electrolightwarehouse.core

open class Resource<out T> constructor(
    val status: ResourceState,
    val data: T?,
    val message: String?
) {

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(ResourceState.SUCCESS, data, null)
        }

        fun <T> error(message: String?): Resource<T> {
            return Resource(ResourceState.ERROR, null, message)
        }

        fun <T> loading(): Resource<T> {
            return Resource(ResourceState.LOADING, null, null)
        }
    }
}

open class ResourceDelete constructor(
    val status: ResourceState,
    val message: String?
) {

    companion object {
        fun success(): ResourceDelete {
            return ResourceDelete(ResourceState.SUCCESS, null)
        }

        fun error(message: String?): ResourceDelete {
            return ResourceDelete(ResourceState.ERROR, message)
        }

        fun loading(): ResourceDelete{
            return ResourceDelete(ResourceState.LOADING, null)
        }
    }
}


enum class ResourceState {
    LOADING, SUCCESS, ERROR
}