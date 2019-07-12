package framework.core.std

internal interface Requester<T> {
    suspend fun get(): T
}
