package com.karan.clock.UI

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.karan.clock.R
import com.karan.clock.databinding.ActivityUpdateAlarmBinding
import java.util.Calendar
import java.util.Locale

class UpdateAlarmActivity : AppCompatActivity() {

    private lateinit var binder : ActivityUpdateAlarmBinding
    private val db : AlarmDataBaseHelper by lazy {
        AlarmDataBaseHelper.getInstance()
    }
    private val adapter : AlarmAdapter by lazy {
        AlarmAdapter(db.getAllAlarms(),this@UpdateAlarmActivity)
    }
    private lateinit var alarm : Alarm
    private  var newHour : Int? = null
    private  var newMinute : Int? = null
    private var newLabel : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = ActivityUpdateAlarmBinding.inflate(layoutInflater)
        setContentView(binder.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val id = intent.getIntExtra("id",-1)
        alarm = db.getAlarmById(id)
        newHour = alarm.hour
        newMinute = alarm.minutes
        newLabel = alarm.label

        binder.apply {
            timeTxt.text = String.format(Locale.getDefault(), "%02d:%02d", (if(alarm.hour==12) 12 else ((alarm.hour)%12)), alarm.minutes)
            labelTxt.editText?.setText(alarm.label)
        }

        binder.timeTxt.setOnClickListener {
            TimePickerDialog(
                this@UpdateAlarmActivity,
                object : TimePickerDialog.OnTimeSetListener {
                    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                        newHour = hourOfDay
                        newMinute = minute
                        binder.timeTxt.text = String.format(Locale.getDefault(), "%02d:%02d", if(hourOfDay==12) 12 else (hourOfDay%12), minute)
                        if(hourOfDay>12) {
                            binder.ampm.text = String.format(Locale.getDefault(),"pm")
                        }else {
                            binder.ampm.text = String.format(Locale.getDefault(),"am")
                        }
                    }
                },
                alarm.hour,
                alarm.minutes,
                false
            ).apply {
                setTitle("Select Time")
                show()
            }
        }

        binder.updateBtn.setOnClickListener {
            newLabel = binder.labelTxt.editText?.text.toString()
            db.updateAlarm(id, newHour, newMinute, newLabel).let {
                if(it>0){
                    Toast.makeText(this@UpdateAlarmActivity, "Saved !", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@UpdateAlarmActivity, "Cannot Add Alarm !", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }

        binder.deleteBtn.setOnClickListener {
            db.deleteAlarm(id).let {
                if(it>0){
                    Toast.makeText(this@UpdateAlarmActivity, "Deleted !", Toast.LENGTH_SHORT).show()
                }else {
                    Toast.makeText(this@UpdateAlarmActivity, "Cannot Delete Alarm !", Toast.LENGTH_SHORT).show()
                }
            }
            finish()
        }

    }
}