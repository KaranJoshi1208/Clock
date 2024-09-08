package com.karan.clock.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.karan.clock.databinding.FragmentClockBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClockFragment : Fragment() {

    private var binder : FragmentClockBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binder = FragmentClockBinding.inflate(layoutInflater,container,false)
        return binder?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binder?.apply {

            dateTextView.text = SimpleDateFormat(
                "EEEE, MMMM dd, yyyy",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)

            addClock.setOnClickListener {
                Toast.makeText(context, "Feature Coming Soon !", Toast.LENGTH_SHORT).show()
            }
        }
    }

}