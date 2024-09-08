package com.karan.clock.UI

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.karan.clock.R
import com.karan.clock.databinding.AlarmItemBinding
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlarmAdapter(
    var allAlarms: MutableList<Alarm>,
    val context: Context
) : RecyclerView.Adapter<AlarmAdapter.AlarmHolder>() {

    private var pendingIntent: PendingIntent? = null
    private var alarmManager : AlarmManager? = null
    companion object {
        private const val DAY_MILLIS : Long = 86400000
    }
    private val db : AlarmDataBaseHelper by lazy {
        AlarmDataBaseHelper.getInstance()
    }

    inner class AlarmHolder(val binder : AlarmItemBinding) : RecyclerView.ViewHolder(binder.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmHolder {
        return AlarmHolder(AlarmItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: AlarmHolder, position: Int) {
        val alarm = allAlarms[position]
        holder.binder.apply {
            timeTxt.text = String.format(Locale.getDefault(),"%02d:%02d",(if(alarm.hour==12) 12 else ((alarm.hour)%12)),alarm.minutes)
            whichHalf.text = if(alarm.hour>12) String.format(Locale.getDefault(),"pm") else String.format(Locale.getDefault(),"am")
            labelTxt.text = alarm.label
            /*
            alarmSwitch.isChecked = alarm.isActive != 0        ,   this is nice way to reduce simple if-else statements
             */

            if(alarm.isActive == 0) {
                alarmSwitch.isChecked = false
            }else {
                alarmSwitch.isChecked = true
                timeTxt.alpha = 1F
                whichHalf.alpha = 1F
                alarmSwitch.alpha = 1F
                labelTxt.alpha = 0.7F
            }
            alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                if(!isChecked) {
                    // cancel alarm
                    cancelAlarm(alarm)
                    timeTxt.alpha = 0.7F
                    whichHalf.alpha = 0.7F
                    alarmSwitch.alpha = 0.7F
                    labelTxt.alpha = 0.4F
                    Toast.makeText(context, "Alarm of ${timeTxt.text} is Canceled !", Toast.LENGTH_SHORT).show()
                }else{
                    // set alarm
                    setAlarm(alarm, labelTxt.text as String)
                    timeTxt.alpha = 1F
                    whichHalf.alpha = 1F
                    alarmSwitch.alpha = 1F
                    labelTxt.alpha = 0.7F
                    Toast.makeText(context, "Alarm of ${timeTxt.text} is NOW Active !!", Toast.LENGTH_SHORT).show()
                }
            }

            layout.setOnClickListener {
                if(alarmSwitch.isChecked){
                    Toast.makeText(context, "To Update Alarm , First Turn it Off", Toast.LENGTH_SHORT).show()
                }
                else {
                    Intent(context , UpdateAlarmActivity::class.java).apply {
                        putExtra("id", alarm.id)
                        putExtra("position" , position)

                        context.startActivity(this)
                        /*
                        The startActivity() method is available in Android,
                        but it's typically a method of the Context class or its subclasses, like Activity or Fragment.
                        In your AlarmAdapter class,
                        you're trying to call startActivity() directly,
                        which isn't possible because RecyclerView.Adapter (and therefore your AlarmAdapter) is not a subclass of Context
                         */
                    }
                }
            }
        }
    }

    override fun getItemCount() = allAlarms.size


    private fun setAlarm(alarm : Alarm, label:String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minutes)
            set(Calendar.SECOND, 0)
        }
        var time = calendar.timeInMillis
        if(time <= Calendar.getInstance().timeInMillis ) time += DAY_MILLIS                          // set Alarm for next day
        pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id,
            Intent(context , AlarmBroadcastReceiver::class.java).apply {
                putExtra("Label", label)
                putExtra("id" , alarm.id)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        try {
            alarmManager!!.setExact(
                AlarmManager.RTC_WAKEUP,
                time,                                                                                    // time when alarm hits (in millis)
                pendingIntent!!
            )
            time -= System.currentTimeMillis()
            Toast.makeText(context , "Alarm will go of after ${TimeUnit.MILLISECONDS.toHours(time) % 60} hours ${TimeUnit.MILLISECONDS.toMinutes(time) % 60} minutes", Toast.LENGTH_LONG).show()
        }
        catch (e : SecurityException){
            Toast.makeText(context , "Cannot schedule exact alarms. Please check app permissions.", Toast.LENGTH_LONG).show()
        }

        db.switchAlarm(alarm.id,1)
    }

    private fun cancelAlarm(alarm : Alarm) {
        if(pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(
                context,
                alarm.id,
                Intent(context , AlarmBroadcastReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
        if(alarmManager == null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        alarmManager!!.cancel(pendingIntent!!)

        db.switchAlarm(alarm.id,0)
    }

    fun refreshAlarms(list : MutableList<Alarm>) {
        this.allAlarms = list
        notifyDataSetChanged()
    }

}
