package com.karan.clock.UI


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.karan.clock.databinding.FragmentAlarmBinding
import java.util.Calendar


class AlarmFragment : Fragment() {

    private lateinit var binder : FragmentAlarmBinding
    private val calender : Calendar by lazy {
        Calendar.getInstance()
    }
    private val db : AlarmDataBaseHelper by lazy {
        AlarmDataBaseHelper.getInstance()
    }
    private lateinit var alarmAdapter : AlarmAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = FragmentAlarmBinding.inflate(layoutInflater, container, false)
        return binder.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alarmAdapter = AlarmAdapter(db.getAllAlarms(),requireContext())

        binder.alarmRV.apply {
            adapter = alarmAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binder.addAlarm.setOnClickListener {
            Intent(context , AddAlarmActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        alarmAdapter.refreshAlarms(db.getAllAlarms())
    }
}
