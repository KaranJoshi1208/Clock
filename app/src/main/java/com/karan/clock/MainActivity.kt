package com.karan.clock

import android.os.Binder
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karan.clock.UI.AlarmDataBaseHelper
import com.karan.clock.UI.AlarmFragment
import com.karan.clock.UI.ClockFragment
import com.karan.clock.UI.StopwatchFragment
import com.karan.clock.UI.TimerFragment
import com.karan.clock.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Clock

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Default)

    private lateinit var binder: ActivityMainBinding
    private lateinit var clockBtn : ImageButton
    private lateinit var stopwatchBtn : ImageButton
    private lateinit var timerBtn : ImageButton
    private lateinit var alarmBtn : ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binder.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clockBtn = findViewById(R.id.clockBtn)
        stopwatchBtn = findViewById(R.id.stopwatchBtn)
        timerBtn = findViewById(R.id.timerBtn)
        alarmBtn = findViewById(R.id.alarmBtn)

        val clockFrag = ClockFragment()
        val stopwatchFrag = StopwatchFragment()
        val timerFrag = TimerFragment()
        val alarmFrag = AlarmFragment()

        AlarmDataBaseHelper.getInstance(this@MainActivity)                                       // Instantiating the singleton database INSTANCE

        clockBtn.setOnClickListener {
            scope.launch {
                launch {
                    setBlue(1)
                }
                launch {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragView, clockFrag)
                        commit()
                    }
                }
            }
        }
        alarmBtn.setOnClickListener {
            scope.launch {
                launch {
                    setBlue(2)
                }
                launch {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragView,alarmFrag)
                        commit()
                    }
                }
            }
        }
        stopwatchBtn.setOnClickListener {
            scope.launch {
                launch {
                    setBlue(3)
                }
                launch {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragView, stopwatchFrag)
                        commit()
                    }
                }
            }
        }
        timerBtn.setOnClickListener {
            scope.launch {
                launch {
                    setBlue(4)

                }
                launch{
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragView, timerFrag)
                        commit()
                    }
                }
            }

        }

    }

    private fun setBlue(num:Int) {

        val icon_clock = AppCompatResources.getDrawable(this, R.drawable.icon_clock)
        val icon_stopwatch = AppCompatResources.getDrawable(this, R.drawable.icon_stopwatch)
        val icon_timer = AppCompatResources.getDrawable(this, R.drawable.icon_hourglass)
        val icon_alarm = AppCompatResources.getDrawable(this, R.drawable.icon_alarm)
        when(num){
            1 -> {
                clockBtn.setImageDrawable(icon_clock?.apply {
                    setTint(getColor(R.color.blue))
                })
                alarmBtn.setImageDrawable(icon_alarm?.apply {
                    setTint(getColor(R.color.black))
                })
                stopwatchBtn.setImageDrawable(icon_stopwatch?.apply {
                    setTint(getColor(R.color.black))
                })
                timerBtn.setImageDrawable(icon_timer?.apply {
                    setTint(getColor(R.color.black))
                })
            }

            2 -> {
                clockBtn.setImageDrawable(icon_clock?.apply {
                    setTint(getColor(R.color.black))
                })
                alarmBtn.setImageDrawable(icon_alarm?.apply {
                    setTint(getColor(R.color.blue))
                })
                stopwatchBtn.setImageDrawable(icon_stopwatch?.apply {
                    setTint(getColor(R.color.black))
                })
                timerBtn.setImageDrawable(icon_timer?.apply {
                    setTint(getColor(R.color.black))
                })
            }

            3 -> {
                clockBtn.setImageDrawable(icon_clock?.apply {
                    setTint(getColor(R.color.black))
                })
                alarmBtn.setImageDrawable(icon_alarm?.apply {
                    setTint(getColor(R.color.black))
                })
                stopwatchBtn.setImageDrawable(icon_stopwatch?.apply {
                    setTint(getColor(R.color.blue))
                })
                timerBtn.setImageDrawable(icon_timer?.apply {
                    setTint(getColor(R.color.black))
                })
            }

            4 -> {
                clockBtn.setImageDrawable(icon_clock?.apply {
                    setTint(getColor(R.color.black))
                })
                alarmBtn.setImageDrawable(icon_alarm?.apply {
                    setTint(getColor(R.color.black))
                })
                stopwatchBtn.setImageDrawable(icon_stopwatch?.apply {
                    setTint(getColor(R.color.black))
                })
                timerBtn.setImageDrawable(icon_timer?.apply {
                    setTint(getColor(R.color.blue))
                })
            }
        }
    }
}