package com.stationhead.rxjava_fizzbuzz.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.stationhead.rxjava_fizzbuzz.R
import io.reactivex.rxkotlin.subscribeBy
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val model: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(
                MainActivityViewModel::class.java)
    }

    // Phase-1 objective:
    // Amber light should turn on every 3 seconds
    // Green light should turn on every 5 seconds
    // Lights will turn when `isActivated` is true

    // Phase-2 objective:
    // integrate flash functionality.
    // update status label to read:
    //    "Fizz" when the amber light flashes,
    //    "Buzz" when the green light flashes,
    //    "FizzBuzz" when both lights flashes, and
    //    nothing ("") when neither light flashes.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.filteredAmber
                .subscribeBy(
                        onNext = {
                            Log.i("Test", "AmberLight: $it")
                            amberLight.isActivated = true
                        })

        model.filteredGreen
                .subscribeBy(
                        onNext = {
                            Log.i("Test", "greenLight: $it")
                            greenLight.isActivated = true
                        })
        model.ticker
                .observeOn(AndroidSchedulers.mainThread())
                .bindToLifecycle(this)
                .subscribe {
                    when (it) {
                        MainActivityViewModel.FlashState.ON -> amberLight.isActivated = true
                        MainActivityViewModel.FlashState.OFF -> amberLight.isActivated = false
                    }
                }
    }
}
