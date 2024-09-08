package com.karan.clock.UI

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karan.clock.R
import com.karan.clock.databinding.ActivityAddAlarmBinding
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AddAlarmActivity : AppCompatActivity() {
    private lateinit var binder: ActivityAddAlarmBinding
    private val alarm : Alarm by lazy {
        Alarm(0,0,0,"",0)
    }
    private val calender : Calendar by lazy {
        Calendar.getInstance()
    }
    private val db : AlarmDataBaseHelper by lazy {
        AlarmDataBaseHelper.getInstance()
    }
    private val adapter : AlarmAdapter by lazy {
        AlarmAdapter(db.getAllAlarms(),this@AddAlarmActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = ActivityAddAlarmBinding.inflate(layoutInflater)
        setContentView(binder.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binder.timeTxt.setOnClickListener {
            TimePickerDialog(
                this@AddAlarmActivity,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        alarm.hour = hourOfDay
                        alarm.minutes = minute
                        binder.timeTxt.text = String.format(Locale.getDefault(), "%02d:%02d", if(hourOfDay==12) 12 else (hourOfDay%12), minute)
                        if(hourOfDay>12) {
                            binder.ampm.text = String.format(Locale.getDefault(),"pm")
                        }else {
                            binder.ampm.text = String.format(Locale.getDefault(),"am")
                        }
                    }
                },
                calender.get(Calendar.HOUR_OF_DAY),
                calender.get(Calendar.MINUTE),
                false
            ).apply {
                setTitle("Select Time")
                show()
            }
        }

        binder.addBtn.setOnClickListener {
            db.insertAlarm(alarm.hour , alarm.minutes , binder.labelTxt.editText?.text.toString().trim(), 0)
            binder.labelTxt.editText?.text?.clear()
            finish()
        }
    }
}