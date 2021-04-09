package com.stationhead.rxjava_fizzbuzz.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.stationhead.rxjava_fizzbuzz.R
import com.trello.rxlifecycle2.android.lifecycle.kotlin.bindToLifecycle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val model: MainActivityViewModel by lazy {
        ViewModelProvider(this).get(
            MainActivityViewModel::class.java
        )
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

    //Phase-3 objective:
    // Integrate Spinners to let user select rate of flashes.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPickers()

        model.filteredAmber
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = {
                    Log.i("Test", "AmberLight: $it")
                    when (it) {
                        MainActivityViewModel.FlashState.ON -> amberLight.isActivated = true
                        MainActivityViewModel.FlashState.OFF -> amberLight.isActivated = false
                    }
                })

        model.filteredGreen
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = {
                    Log.i("Test", "greenLight: $it")
                    when (it) {
                        MainActivityViewModel.FlashState.ON -> greenLight.isActivated = true
                        MainActivityViewModel.FlashState.OFF -> greenLight.isActivated = false
                    }
                })

        model.fizzBuzz
            .observeOn(AndroidSchedulers.mainThread())
            .bindToLifecycle(this)
            .subscribeBy(
                onNext = { (amberStatus, greenStatus) ->
                    Log.i("Test", "amberState: $amberStatus greenState: $greenStatus")
                    val text = when {
                        amberStatus == MainActivityViewModel.FlashState.ON &&
                                greenStatus == MainActivityViewModel.FlashState.ON -> "FizzBuzz"
                        amberStatus == MainActivityViewModel.FlashState.ON -> "Fizz"
                        greenStatus == MainActivityViewModel.FlashState.ON -> "Buzz"
                        else -> ""
                    }
                    statusLabel.text = text
                }
            )
    }

    private fun setupPickers() {
        leftPicker.minValue = 3
        leftPicker.maxValue = 9
        leftPicker.wrapSelectorWheel = false

        rightPicker.minValue = 5
        rightPicker.maxValue = 13
        rightPicker.wrapSelectorWheel = false

        // emit min values to set as default
        model.leftPickerValueUpdated(leftPicker.minValue)
        model.rightPickerValueUpdated(rightPicker.minValue)

        leftPicker.setOnValueChangedListener { _, _, newVal ->
            model.leftPickerValueUpdated(newVal)
        }
        rightPicker.setOnValueChangedListener { _, _, newVal ->
            model.rightPickerValueUpdated(newVal)
        }
    }
}
