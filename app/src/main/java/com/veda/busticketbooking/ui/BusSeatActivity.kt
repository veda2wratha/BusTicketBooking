package com.veda.busticketbooking.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.veda.busticketbooking.R
import com.veda.busticketbooking.databinding.ActivityBusSeatBinding
import com.veda.busticketbooking.db.entities.BusSeatEntity
import com.veda.busticketbooking.utils.MyUtils
import com.veda.busticketbooking.utils.MyUtils.Companion.SEAT_AVAILABLE
import com.veda.busticketbooking.utils.MyUtils.Companion.SEAT_COUNT_PER_COLUMN
import com.veda.busticketbooking.utils.MyUtils.Companion.SEAT_NOT_AVAILABLE
import com.veda.busticketbooking.utils.MyUtils.Companion.SEAT_SELECTED
import dagger.hilt.android.AndroidEntryPoint
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

        // Adding data to local DB
        viewModel.insertBusSeats()
        initVM()
    }

        private fun initVM() {
            viewModel.getRecordsObserver().observe(this
            ) { t ->
                if (t != null) {
                    setRoomListAdapter(t)
                }
            }
        }

    private fun setRoomListAdapter(dataSet: List<BusSeatEntity>) {
        adapter = SeatListAdapter(dataSet, this)
        binding.recycler.layoutManager = GridLayoutManager(this, SEAT_COUNT_PER_COLUMN)
        binding.recycler.adapter = adapter
        binding.progressBar.visibility = View.GONE
    }

    override fun onItemClick(seat: BusSeatEntity, position: Int) {
        if(seat.seatStatus.equals(MyUtils.SEAT_NOT_AVAILABLE)){
            Toast.makeText(this, getString(R.string.seat_not_available), Toast.LENGTH_SHORT).show()
        }else{
            seat.let {
                if (it.seatStatus.equals(SEAT_AVAILABLE)) {
                    it.seatStatus = SEAT_SELECTED
                } else if (it.seatStatus.equals(SEAT_SELECTED)) {
                    it.seatStatus = SEAT_AVAILABLE
                }
                viewModel.updateBusSeat(seat)
                adapter.notifyItemChanged(position)

                showPersonDetailsBottomSheet(seat, position)
            }
        }

    }

    fun showPersonDetailsBottomSheet(seat:BusSeatEntity, position : Int){

        val dialog = this.let { BottomSheetDialog(it) }

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
                if(it.seatStatus.equals(SEAT_SELECTED)){
                    it.seatStatus = SEAT_NOT_AVAILABLE
                    it.dateOfBooking = "$myYear/$myMonth/$myDay"
                    it.timeOfBooking = "$myHour:$myMinute"
                    it.remindBefore = reminder.text.toString()

                    // updating seat status
                    viewModel.updateBusSeat(seat)
                    adapter.notifyItemChanged(position)
                }

                // Creating calendar instance for notification
                val calendar = Calendar.getInstance().apply {
                    set(myYear,myMonth,myDay,myHour,myMinute- it.remindBefore.toInt(),0)
                }
                viewModel.setReminder(it, calendar)
            }
            dialog.dismiss()
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