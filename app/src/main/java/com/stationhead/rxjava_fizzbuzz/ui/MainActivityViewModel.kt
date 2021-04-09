package com.stationhead.rxjava_fizzbuzz.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.stationhead.rxjava_fizzbuzz.usecase.TickProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class MainActivityViewModel : ViewModel() {

    val fizzCount = BehaviorSubject.create<Int>()
    val buzzCount = BehaviorSubject.create<Int>()

    public override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    val leftPickerValueUpdated = fizzCount::onNext

    val rightPickerValueUpdated = buzzCount::onNext

    private val disposables by lazy { CompositeDisposable() }

    val ticker = TickProvider().getTicks()

    val count: Observable<Int> = ticker.scan(0) { accumulator, current ->
        accumulator + 1
    }.doOnNext { Log.i("model", "$it") }

    val filteredAmber = Observables.combineLatest(count, fizzCount)
        .doOnNext { (count, fizzCount) -> Log.i("model", "amber $count, $fizzCount") }
        .filter { (count, fizzCount) -> count % fizzCount == 0 }
        .flash()

    val filteredGreen = Observables.combineLatest(count, buzzCount)
        .doOnNext { (count, buzzCount) -> Log.i("model", "amber $count, $buzzCount") }
        .filter { (count, buzzCount) -> count % buzzCount == 0 }
        .flash()

    val fizzBuzz = Observables.combineLatest(filteredAmber, filteredGreen)

    private fun Observable<*>.flash() = flatMap { _ ->
        Observable.just(FlashState.ON)
            .mergeWith(Observable.timer(400, TimeUnit.MILLISECONDS).map { _ -> FlashState.OFF })
    }

    enum class FlashState { ON, OFF }
}
