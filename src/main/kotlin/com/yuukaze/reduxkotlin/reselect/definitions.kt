package com.yuukaze.reduxkotlin.reselect


typealias TStateMap<T,State> = (State)->T
typealias TAction<T> = (T)->Unit