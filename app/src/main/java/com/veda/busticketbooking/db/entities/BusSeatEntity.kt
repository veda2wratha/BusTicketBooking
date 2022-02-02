package com.veda.busticketbooking.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bus_seat")
data class BusSeatEntity(
    @PrimaryKey(autoGenerate = false)  val id : String,
    var seatStatus: String,
    var dateOfBooking : String,
    var timeOfBooking:String,
    var remindBefore:String)

    // 0 - not available - green
    // 1 - available - orange
    // 2 - selected - blue