package com.veda.busticketbooking.ui

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.veda.busticketbooking.R
import com.veda.busticketbooking.broadcasts.BusSeatNotificationReceiver
import com.veda.busticketbooking.databinding.ActivityBusSeatBinding
import com.veda.busticketbooking.db.entities.BusSeatEntity
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParsePosition
import java.util.*


@AndroidEntryPoint
class BusSeatActivity : AppCompatActivity(), ItemClickListener, DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var viewModel: BusSeatViewModel
    private lateinit var adapter: SeatListAdapter
    private lateinit var binding: ActivityBusSeatBinding
    private lateinit var date :TextView
    private lateinit var time :TextView
    private lateinit var reminder : TextInputEditText

    private val ALARM_REQUEST_CODE = 1000

    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_seat)

        binding = ActivityBusSeatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this).get(BusSeatViewModel::class.java)

        binding.progressBar.visibility = View.VISIBLE

        // Adding data to loacl DB
        viewModel.insertBusSeats()
        initVM()
    }

        private fun initVM() {
            viewModel.getRecordsObserver().observe(this, object : Observer<List<BusSeatEntity>> {
                override fun onChanged(t: List<BusSeatEntity>?) {
                    if (t != null) {
                        setRoomListAdapter(t)
                    }
                }
            })
        }

    private fun setRoomListAdapter(dataSet: List<BusSeatEntity>) {
        adapter = SeatListAdapter(dataSet, this)
        binding.recycler.layoutManager = GridLayoutManager(this, 3)
        binding.recycler.adapter = adapter
        binding.progressBar.visibility = View.GONE
    }

    override fun onItemClick(seat: BusSeatEntity, position: Int) {
        if(seat.seatStatus.equals("0")){
            Toast.makeText(this, "Seat not available", Toast.LENGTH_SHORT).show()
        }else{
            seat.let {
                if (it.seatStatus.equals("1")) {
                    it.seatStatus = "2"
                } else if (it.seatStatus.equals("2")) {
                    it.seatStatus = "1"
                }
                viewModel.updateBusSeat(seat)
                adapter.notifyItemChanged(position)

                showPersonDetailsBottomSheet(seat, position)
            }
        }

    }

    fun showPersonDetailsBottomSheet(seat:BusSeatEntity, position : Int){

        val dialog = this?.let { BottomSheetDialog(it) }

        val view = layoutInflater.inflate(R.layout.layout_seat_book, null)

        date = view.findViewById<TextView>(R.id.date)
        time = view.findViewById<TextView>(R.id.time)
        reminder = view.findViewById<TextInputEditText>(R.id.remindHour)

        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnCancel= view.findViewById<Button>(R.id.btnCancel)

        date.setOnClickListener {

            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this, this, year, month,day)
            datePickerDialog.show()
        }

        btnConfirm.setOnClickListener {
            seat.let {
                if(it.seatStatus.equals("2")){
                    it.seatStatus = "0"
                    it.dateOfBooking = "$myYear/$myMonth/$myDay"
                    it.timeOfBooking = "$myHour:$myMinute"
                    it.remindBefore = reminder.text.toString()
                    viewModel.updateBusSeat(seat)
                    adapter.notifyItemChanged(position)
                }
                setReminder(it)
            }
            dialog?.dismiss()
        }

        btnCancel.setOnClickListener {
            seat.let {
                if(it.seatStatus.equals("2")){
                    it.seatStatus = "1"
                    viewModel.updateBusSeat(seat)
                    adapter.notifyItemChanged(position)
                }
            }
            dialog?.dismiss()
        }

        dialog!!.setCancelable(true)
        dialog.setContentView(view)

        dialog.show()
    }

    private fun setReminder(it: BusSeatEntity) {
        val myMin = myMinute- it.remindBefore.toInt();

        val calendar = Calendar.getInstance().apply {
            set(myYear,myMonth,myDay,myHour,myMin,0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, BusSeatNotificationReceiver::class.java)
        intent.putExtra("bus_date",it.dateOfBooking)
        intent.putExtra("bus_time",it.timeOfBooking)
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(this, "Booking Successful, Will Remind you before ${it.remindBefore} minute", Toast.LENGTH_SHORT).show();
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = day
        myYear = year
        myMonth = month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute

        date.text = "Booking Date : $myYear/$myMonth/$myDay"
        time.text = "Booking Time $myHour:$myMinute"
    }
}