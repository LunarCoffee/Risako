package dev.lunarcoffee.risako.framework.core.dispatchers

interface Dispatchable<T : DispatchableContext, U : DispatchableArgs> {
    suspend fun dispatch(ctx: T, args: U)
}
