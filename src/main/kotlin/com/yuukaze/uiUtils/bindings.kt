package com.yuukaze.uiUtils

import com.yuukaze.reduxkotlin.reselect.reselect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.reduxkotlin.Store

@ExperimentalCoroutinesApi
fun <State : Any, T> Store<State>.createReduxBindData(
    default: T,
    setter: (State) -> T
): StateFlow<T> {
    val wrapper = MutableStateFlow(default)
    this.reselect(setter) {
        wrapper.value = it
    }
    return wrapper
}