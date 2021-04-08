package com.stationhead.rxjava_fizzbuzz.ui

import androidx.lifecycle.ViewModel
import com.stationhead.rxjava_fizzbuzz.usecase.TickProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    public override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    private val disposables by lazy { CompositeDisposable() }

    val ticker = TickProvider().getTicks()

    val count: Observable<Int> = ticker.scan(0) { accumulator, current ->
        accumulator + 1
    }

    val filteredAmber = count.filter {
        it % 3 == 0
    }.flash()

    val filteredGreen = count.filter {
        it % 5 == 0
    }.flash()

    private fun Observable<*>.flash() = flatMap { _ ->
        Observable.just(FlashState.ON)
            .mergeWith(Observable.timer(400, TimeUnit.MILLISECONDS).map { _ -> FlashState.OFF })
    }

    enum class FlashState { ON, OFF }
}