package com.yuukaze.reduxkotlin.reselect

import kotlin.reflect.KFunction


typealias TStateMap<T, State> = (State) -> T
typealias TAction<T> = (T) -> Unit

interface KFunctionVarArg<in P1, out R> : KFunction<R> {
  fun invoke(vararg p1: P1): R
}