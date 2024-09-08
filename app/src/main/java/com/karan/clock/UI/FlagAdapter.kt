package com.karan.clock.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.karan.clock.R
import java.util.Locale
import java.util.concurrent.TimeUnit

class FlagAdapter(
    var allFlags : MutableList<Long>,
    val context: Context
) : RecyclerView.Adapter<FlagAdapter.FlagHolder>() {

    inner class FlagHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val serialNo : TextView = itemView.findViewById(R.id.serialNo)
        val leadTxt : TextView = itemView.findViewById(R.id.leadTxt)
        val timeTxt : TextView = itemView.findViewById(R.id.timeTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlagHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flag_item, parent, false)
        return FlagHolder(view)
    }

    override fun onBindViewHolder(holder: FlagHolder, position: Int) {
        holder.apply {
            serialNo.text = (position + 1).toString()
            leadTxt.text = calcLead(position)
            timeTxt.text = calcTime(allFlags[position])
        }
    }

    override fun getItemCount() = allFlags.size

    private fun calcLead(position: Int): String {
        if(position == 0){
            return "+ 00:00.000"
        }
        else {
            val diff = (allFlags[position] - allFlags[position - 1])
            val mins = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
            val secs = TimeUnit.MILLISECONDS.toSeconds(diff) % 60
            val millis = (diff % 1000)
            return String.format(Locale.getDefault(), "+ %02d:%02d.%03d" , mins, secs, millis)
        }
    }


    private fun calcTime(flag : Long): String {
        val mins = TimeUnit.MILLISECONDS.toMinutes(flag) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(flag) % 60
        val millis = (flag % 1000)
        return String.format(Locale.getDefault(), "%02d:%02d.%03d" , mins, secs, millis)
    }
}
