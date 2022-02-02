package com.veda.busticketbooking.db.dao

import androidx.room.*
import com.veda.busticketbooking.db.entities.BusSeatEntity


@Dao
interface BusSeatDAO {

    @Query("SELECT * FROM bus_seat")
    fun getAllBusSeats(): List<BusSeatEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBusData(seatList: List<BusSeatEntity>)

    @Delete
    fun deleteBusSeat(seat: BusSeatEntity?)

    @Update
    fun updateBusSeat(seat: BusSeatEntity?)

}