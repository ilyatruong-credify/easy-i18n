package com.yuukaze.reduxkotlin.reselect

import org.reduxkotlin.Selector
import org.reduxkotlin.Store

class SelectorSubscriberBuilder<State : Any,T>(val store: Store<State>) {

  val selectorList = mutableMapOf<Selector<State, T>, (T) -> Unit>()

  // state is here to make available to lambda with receiver in DSL
  val state: State
    get() = store.getState()

  var withAnyChangeFun: (() -> Unit)? = null

  fun withAnyChange(f: () -> Unit) {
    withAnyChangeFun = f
  }

  fun select(selector: TStateMap<T,State>, action: TAction<T>) {
    val selBuilder = TypedSelectorBuilder<State>()
    val sel = selBuilder.withSingleField(selector)
    selectorList[sel] = action
  }
}