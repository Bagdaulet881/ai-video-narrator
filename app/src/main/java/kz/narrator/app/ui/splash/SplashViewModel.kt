package kz.narrator.app.ui.splash

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kz.narrator.app.data.preferences.Preferences
import kz.narrator.app.utils.live_data.Event
import javax.inject.Inject

class SplashViewModel
@Inject constructor(
    private val app: Application,
    private val preferences: Preferences
) : AndroidViewModel(app) {

    companion object {
        private const val MILLIS_IN_FUTURE = 1 * 1000L
        private const val INTERVAL = 1000L
    }

    init {
        startTimer()
    }

    private var countDownTimer: CountDownTimer? = null

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(
            MILLIS_IN_FUTURE,
            INTERVAL
        ) {
            override fun onFinish() {
                openOnboard()
            }

            override fun onTick(millisUntilFinished: Long) {
                //do nothing
            }
        }.start()
    }

    private val _openOnboard = MutableLiveData<Event<Unit>>()
    val openOnboard: LiveData<Event<Unit>>
        get() = _openOnboard

    private val _openMainActivity = MutableLiveData<Event<Unit>>()
    val openMainActivity: LiveData<Event<Unit>>
        get() = _openMainActivity

    private fun openOnboard() {
        _openMainActivity.postValue(Event(Unit))
    }

    override fun onCleared() {
        countDownTimer?.cancel()
        super.onCleared()
    }

    fun onOpenMainActivity() {
        _openMainActivity.postValue(Event((Unit)))
    }

}