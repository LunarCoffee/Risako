package dev.lunarcoffee.risako.framework.core.dispatchers

internal interface Dispatchable<T : DispatchableContext, U : DispatchableArgs> {
    suspend fun dispatch(ctx: T, args: U)
}
