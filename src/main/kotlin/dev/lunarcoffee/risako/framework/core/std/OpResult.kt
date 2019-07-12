package dev.lunarcoffee.risako.framework.core.std

sealed class OpResult<T>

class OpSuccess<T>(val result: T) : OpResult<T>()
class OpError<T> : OpResult<T>()
