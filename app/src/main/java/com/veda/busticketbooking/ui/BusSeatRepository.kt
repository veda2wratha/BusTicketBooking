package com.veda.busticketbooking.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.veda.busticketbooking.broadcasts.BusSeatNotificationReceiver
import com.veda.busticketbooking.db.dao.BusSeatDAO
import com.veda.busticketbooking.db.entities.BusSeatEntity
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class BusSeatRepository @Inject constructor(private val appDao: BusSeatDAO, private val context: Context) {

    fun getRecords(): List<BusSeatEntity>? {
        return appDao.getAllBusSeats()
    }

    fun updateRecords(seat: BusSeatEntity){
        return appDao.updateBusSeat(seat)
    }

    fun insertRecords() {

        val seatsList = ArrayList<BusSeatEntity>()

        for (index in 1..21){
            val seatEntity = BusSeatEntity(index.toString(),"1","","","")
            seatsList.add(seatEntity)
        }

        appDao.insertBusData(seatsList)
    }


    fun setReminder(it: BusSeatEntity, calendar : Calendar) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, BusSeatNotificationReceiver::class.java)
        intent.putExtra("bus_date",it.dateOfBooking)
        intent.putExtra("bus_time",it.timeOfBooking)
        val pendingIntent = PendingIntent.getBroadcast(context, 1000,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "Booking Successful, Will Remind you before ${it.remindBefore} minute", Toast.LENGTH_SHORT).show();
    }
}