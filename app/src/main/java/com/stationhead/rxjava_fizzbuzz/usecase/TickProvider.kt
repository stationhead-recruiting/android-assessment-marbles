package com.stationhead.rxjava_fizzbuzz.usecase

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class TickProvider {
    public  fun getTicks() = Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation()).map { _ -> Unit }
}