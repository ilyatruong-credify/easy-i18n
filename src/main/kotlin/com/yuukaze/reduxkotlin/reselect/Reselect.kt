package com.yuukaze.reduxkotlin.reselect

import org.reduxkotlin.*

private typealias EqualityCheckFn = (a: Any, b: Any) -> Boolean

class TypedSelectorBuilder<S : Any> {
  fun <I> withSingleField(fn: S.() -> I) = object : AbstractSelector<S, I>() {
    @Suppress("UNCHECKED_CAST")
    private val inputField =
      InputField(fn, byRefEqualityCheck) as SelectorInput<Any, Any>
    override val computeAndCount = fun(i: Array<out Any>): I {
      ++_recomputations
      @Suppress("UNCHECKED_CAST")
      return i[0] as I
    }

    override operator fun invoke(state: S): I {
      return memoizer.memoize(state, inputField)
    }

    override val equalityCheck: EqualityCheckFn
      get() = byRefEqualityCheck
    override val memoizer: Memoizer<I> by lazy {
      singleInputMemoizer(computeAndCount)
    }
  }

  fun <I> withSingleFieldByValue(fn: S.() -> I) =
    object : AbstractSelector<S, I>() {
      @Suppress("UNCHECKED_CAST")
      private val inputField =
        InputField(fn, byValEqualityCheck) as SelectorInput<Any, Any>
      override val computeAndCount = fun(i: Array<out Any>): I {
        ++_recomputations
        @Suppress("UNCHECKED_CAST")
        return i[0] as I
      }

      override operator fun invoke(state: S): I {
        return memoizer.memoize(state, inputField)
      }

      override val equalityCheck: EqualityCheckFn
        get() = byValEqualityCheck
      override val memoizer: Memoizer<I> by lazy {
        singleInputMemoizer(computeAndCount)
      }

      operator fun <I : Any> invoke(fn: S.() -> I): AbstractSelector<S, I> {
        return withSingleField(fn)
      }
    }
}

private fun <State : Any, T> Store<State>.reselectors(selectorSubscriberBuilderInit: SelectorSubscriberBuilder<State, T>.() -> Unit): StoreSubscriber {
  val subscriberBuilder = SelectorSubscriberBuilder<State, T>(this)
  subscriberBuilder.selectorSubscriberBuilderInit()
  val sub = {
    subscriberBuilder.selectorList.forEach { entry ->
      entry.key.onChangeIn(getState()) { entry.value(it) }
    }
    subscriberBuilder.withAnyChangeFun?.invoke()
    Unit
  }
  // call subscriber immediately when subscribing
  sub()
  return this.subscribe(sub)
}


fun <State : Any, T> Store<State>.reselect(
  selector: TStateMap<T, State>,
  action: TAction<T>
): StoreSubscriber {
  return this.reselectors<State, T> {
    select(selector, action)
  }
}

fun <State : Any, T> Store<State>.reselect(
  selector: TStateMap<T, State>
) = fun(action: TAction<T>): StoreSubscriber {
  return this.reselect(selector, action)
}