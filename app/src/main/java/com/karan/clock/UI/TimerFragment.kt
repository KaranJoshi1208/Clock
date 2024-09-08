package com.karan.clock.UI

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import com.karan.clock.R
import com.karan.clock.databinding.FragmentTimerBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimerFragment : Fragment() {

    private lateinit var binder : FragmentTimerBinding
    private var totalTime : Long = 0L

    private var isRunning = false
    private val scope : CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Default)
    }
    private var elapsedAtPause : Long = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentTimerBinding.inflate(layoutInflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binder.progressBar.apply {
            isIndeterminate = false
            max = 100
        }

        binder.secs.minValue = 0
        binder.secs.maxValue = 59

        binder.mins.minValue = 0
        binder.mins.maxValue = 59

        binder.hours.minValue = 0
        binder.hours.maxValue = 23


        binder.timer.onChronometerTickListener =
            Chronometer.OnChronometerTickListener { chronometer ->
                val remainingTime = chronometer!!.base - SystemClock.elapsedRealtime()
                if (remainingTime <= 0L) {
                    // Time is up
                    Toast.makeText(context, "Times up !", Toast.LENGTH_SHORT).show()
                    chronometer.stop()
                    binder.progressBar.progress = 0
                    resetUI()
                } else {
                    // Update the progress bar
                    binder.progressBar.progress = (remainingTime.toDouble() / totalTime * 100).toInt()
                }
            }

        binder.startBtn.setOnClickListener {
            scope.launch(Dispatchers.Main) {
                launch {
                    val secV = binder.secs.value
                    val minV = binder.mins.value
                    val hourV = binder.hours.value
                    totalTime = SystemClock.elapsedRealtime() + ((secV * 1000 ) + (minV * 60000) + (hourV * 3600000))
                    binder.timer.apply {
                        base = totalTime
                        isCountDown = true
                        start()
                    }
                }

                launch {
                    isRunning = true
                    binder.apply {
                        progressBar.progress = 0
                        timer.visibility = View.VISIBLE
                        playPauseBtn.visibility = View.VISIBLE
                        stopBtn.visibility = View.VISIBLE
                        progressBar.visibility = View.VISIBLE

                        startBtn.visibility = View.INVISIBLE
                        hours.visibility = View.INVISIBLE
                        mins.visibility = View.INVISIBLE
                        secs.visibility = View.INVISIBLE
                    }
                }
            }
        }

        binder.playPauseBtn.setOnClickListener {
            if(isRunning) {
                elapsedAtPause = SystemClock.elapsedRealtime() - binder.timer.base
                binder.timer.stop()
                isRunning = false
                binder.playPauseBtn.setImageResource(R.drawable.icon_play)
            }
            else {
                binder.timer.apply {
                    base = SystemClock.elapsedRealtime() - elapsedAtPause
                    start()
                }
                isRunning = true
                binder.playPauseBtn.setImageResource(R.drawable.icon_pause)
            }
        }

        binder.stopBtn.setOnClickListener {
            scope.launch {
                launch {
                    binder.timer.apply {
                        stop()
                    }
                }
                launch(Dispatchers.Main) {
                   resetUI()
                }
            }
        }

    }

    private fun   resetUI() {
        binder.apply {
            timer.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            stopBtn.visibility = View.INVISIBLE
            playPauseBtn.visibility = View.INVISIBLE

            startBtn.visibility = View.VISIBLE
            hours.visibility = View.VISIBLE
            mins.visibility = View.VISIBLE
            secs.visibility = View.VISIBLE
        }
        isRunning = false
        elapsedAtPause = 0L
    }

}