package com.karan.clock.UI

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.karan.clock.R
import com.karan.clock.databinding.FragmentStopwatchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit

class StopwatchFragment : Fragment() {


    private var isRunning : Boolean = false
    private var elapsedPaused : Long = 0
    private lateinit var flagAdapter : FlagAdapter

    private val allFlags = mutableListOf<Long>()
    private val scope = CoroutineScope(Dispatchers.Default)
    private lateinit var binder : FragmentStopwatchBinding
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable : Runnable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentStopwatchBinding.inflate(layoutInflater,container,false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        flagAdapter = FlagAdapter(allFlags, requireContext())                                                 // Initialising Adapter
        binder.flagRecyclerView.apply {
            adapter = flagAdapter
            layoutManager = LinearLayoutManager(context)
        }


        updateRunnable = Runnable {
            if(isRunning) {
                val elapsedMillis = SystemClock.elapsedRealtime() - binder.chronometer.base
                val mins = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
                val secs = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
                val millis = (elapsedMillis % 1000)
                binder.chronometer.text = String.format(Locale.getDefault(), "%02d:%02d.%03d", mins, secs, millis)
            }
            handler.postDelayed(updateRunnable,10L)                                        // this is recursion , damn !!!
        }

//        binder.chronometer.onChronometerTickListener = Chronometer.OnChronometerTickListener {    // this is slow on updating UI , Results in discrete update of milli seconds (Looks Shit)
//            val elapsedMillis = SystemClock.elapsedRealtime() - binder.chronometer.base
//            val mins = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
//            val secs = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
//            val millis = elapsedMillis % 1000
//            binder.chronometer.text = String.format("$mins: $secs.$millis")
//        }


        binder.startBtn.setOnClickListener {
            scope.launch(Dispatchers.Main) {
                launch {
                    binder.chronometer.apply {
                        base = SystemClock.elapsedRealtime()
                        start()
                        isRunning = true
                        handler.postDelayed(updateRunnable, 10L)
                    }
                }
                launch {
                    binder.startBtn.visibility = View.INVISIBLE
                    binder.flagBtn.visibility = View.VISIBLE
                    binder.playPauseBtn.visibility = View.VISIBLE
                    binder.playPauseBtn.setImageResource(R.drawable.icon_pause)
                    binder.flagBtn.setImageResource(R.drawable.icon_flag)
                }
            }
        }

        binder.playPauseBtn.setOnClickListener {
            if(isRunning){
                elapsedPaused = SystemClock.elapsedRealtime() - binder.chronometer.base
                binder.chronometer.stop()
                isRunning = false
                binder.playPauseBtn.setImageResource(R.drawable.icon_play)
                binder.flagBtn.setImageResource(R.drawable.icon_stop)
            }else{
                binder.chronometer.base = SystemClock.elapsedRealtime()- elapsedPaused
                binder.chronometer.start()
                isRunning = true
                binder.playPauseBtn.setImageResource(R.drawable.icon_pause)
                binder.flagBtn.setImageResource(R.drawable.icon_flag)
            }
        }

        binder.flagBtn.setOnClickListener {
            if(isRunning) {
                val value = (SystemClock.elapsedRealtime() - binder.chronometer.base)
                allFlags.add(value)
                flagAdapter.notifyItemInserted(allFlags.size-1)
            }else{
                binder.chronometer.apply {
                    base = SystemClock.elapsedRealtime()
                    stop()
                }
                flagAdapter.notifyItemRangeRemoved(0,allFlags.size)
                allFlags.clear()
                binder.startBtn.visibility = View.VISIBLE
                binder.flagBtn.visibility = View.INVISIBLE
                binder.playPauseBtn.visibility = View.INVISIBLE
            }
        }
    }
}