package dev.lunarcoffee.risako.framework.core.std

interface Requester<T> {
    suspend fun get(): T
}
