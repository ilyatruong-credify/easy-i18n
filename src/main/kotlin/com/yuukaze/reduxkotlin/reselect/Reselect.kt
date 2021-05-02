@file:Suppress("FunctionName")

package com.yuukaze.reduxkotlin.reselect

import org.reduxkotlin.Store
import org.reduxkotlin.StoreSubscriber

private typealias EqualityCheckFn = (a: Any, b: Any) -> Boolean

/**
 * A rewrite for kotlin of https://github.com/reactjs/reselect library for redux (https://github.com/reactjs/redux)
 * see also "Computing Derived Data" in redux documentation http://redux.js.org/docs/recipes/ComputingDerivedData.html
 * Created by Dario Elyasy  on 3/18/2016.
 */
/**
 * equality check by reference
 */
val byRefEqualityCheck: EqualityCheckFn = { a: Any, b: Any -> a === b }

/**
 * equality check by value: for primitive type
 */
val byValEqualityCheck: EqualityCheckFn = { a: Any, b: Any -> a == b }

interface Memoizer<T> {
  fun memoize(state: Any, vararg inputs: SelectorInput<Any, Any>): T
}

// {a:Any,b:Any -> a===b}
@Suppress("UNCHECKED_CAST")
fun <T> computationMemoizer(computeFn: (Array<out Any>) -> T) =
  object : Memoizer<T> {
    var lastArgs: Array<out Any>? = null
    var lastResult: T? = null
    override fun memoize(
      state: Any,
      vararg inputs: SelectorInput<Any, Any>
    ): T {
      val nInputs = inputs.size
      val args = Array<Any>(nInputs) { inputs[it].invoke(state) }
      if (lastArgs != null && lastArgs!!.size == inputs.size) {
        var bMatchedArgs = true
        for (i in 0 until nInputs) {
          if (!inputs[i].equalityCheck(args[i], lastArgs!![i])) {
            bMatchedArgs = false
            break
          }
        }
        if (bMatchedArgs) {
          return lastResult as T
        }
      }
      lastArgs = args
      lastResult = computeFn(args)
      return lastResult as T
    }
  }

/**
 * specialization for the case of single input (a little bit faster)
 */
@Suppress("UNCHECKED_CAST")
fun <T> singleInputMemoizer(func: (Array<out Any>) -> T) =
  object : Memoizer<T> {
    var lastArg: Any? = null
    var lastResult: T? = null
    override fun memoize(
      state: Any,
      vararg inputs: SelectorInput<Any, Any>
    ): T {
      val input = inputs[0]
      val arg = input.invoke(state)
      if (lastArg != null &&
        input.equalityCheck(arg, lastArg!!)
      ) {
        return lastResult as T
      }
      lastArg = arg
      lastResult = func(arrayOf(arg))
      return lastResult as T
    }
  }

interface SelectorInput<S, I> {
  operator fun invoke(state: S): I
  val equalityCheck: EqualityCheckFn
}

/**
 * a selector function is a function that map a field in state object to the input for the selector compute function
 */
class InputField<S, I>(
  val fn: S.() -> I,
  override val equalityCheck: EqualityCheckFn
) : SelectorInput<S, I> {
  override operator fun invoke(state: S): I = state.fn()
}

/**
 * note: [Selector] inherit from [SelectorInput] because of support for composite selectors
 */
interface Selector<S, O> : SelectorInput<S, O> {
  val recomputations: Long
  fun isChanged(): Boolean

  /**
   * by calling this method, you will force the next call to [getIfChangedIn] to succeed,
   * as if the actual value of the selector was changed, but no actual recomputation is performed
   */
  fun signalChanged()
  fun resetChanged()
  fun getIfChangedIn(state: S): O? {
    val res = invoke(state)
    if (isChanged()) {
      resetChanged()
      return res
    }
    return null
  }

  fun onChangeIn(state: S, blockfn: (O) -> Unit) {
    getIfChangedIn(state)?.let(blockfn)
  }
}

/**
 * abstract base class for all selectors
 */
abstract class AbstractSelector<S, O> : Selector<S, O> {
  @JvmField
  protected var recomputationsLastChanged = 0L

  @JvmField
  protected var _recomputations = 0L
  override val recomputations: Long get() = _recomputations

  /**
   * see documentation to [Selector.signalChanged]
   */
  override fun signalChanged() {
    ++_recomputations
  }

  override fun isChanged(): Boolean =
    _recomputations != recomputationsLastChanged

  override fun resetChanged() {
    recomputationsLastChanged = _recomputations
  }

  protected abstract val computeAndCount: (i: Array<out Any>) -> O

  /**
   * 'lazy' because computeandcount is abstract. Cannot reference to it before it is initialized in concrete selectors
   * 'open' because we can provide a custom memoizer if needed
   */
  open val memoizer by lazy { computationMemoizer(computeAndCount) }
}

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

private fun <State : Any, T> Store<State>._reselectors(
  init: SelectorSubscriberBuilder<State, T>.() -> Unit
): StoreSubscriber {
  val subscriberBuilder = SelectorSubscriberBuilder<State, T>(this)
  subscriberBuilder.init()
  val sub = {
    subscriberBuilder.selectorList.forEach { entry ->
      entry.key.onChangeIn(state) { entry.value(it) }
    }
    subscriberBuilder.withAnyChangeFun?.invoke()
    Unit
  }
  // call subscriber immediately when subscribing
  sub()
  return this.subscribe(sub)
}

//fun <State : Any, T1, T2, T> Store<State>.reselectors(
//  initT1: TStateMap<T1, State>,
//  initT2: TStateMap<T2, State>,
//  combiner: (T1, T2) -> T
//) = @Suppress("UNCHECKED_CAST")
//fun(action: TAction<T>): StoreSubscriber =
//  this.reselectors1(listOf(initT1, initT2), combiner as KFunction<T>, action)
//
//fun <State : Any, T1, T2, T3, T> Store<State>.reselectors(
//  initT1: TStateMap<T1, State>,
//  initT2: TStateMap<T2, State>,
//  initT3: TStateMap<T3, State>,
//  combiner: (T1, T2, T3) -> T
//) = @Suppress("UNCHECKED_CAST")
//fun(action: TAction<T>): StoreSubscriber =
//  this.reselectors1(listOf(initT1, initT2), combiner as KFunction<T>, action)
//
//private fun <State : Any, T> Store<State>.reselectors1(
//  init: List<TStateMap<*, State>>,
//  combiner: KFunction<T>,
//  action: TAction<T>
//): StoreSubscriber =
//  TODO()

fun <State : Any, T> Store<State>.reselect(
  selector: TStateMap<T, State>,
  action: TAction<T>
): StoreSubscriber {
  return this._reselectors<State, T> {
    reselect(selector, action)
  }
}

fun <State : Any, T> Store<State>.reselect(
  selector: TStateMap<T, State>
) = fun(action: TAction<T>): StoreSubscriber {
  return this.reselect(selector, action)
}