package com.veda.busticketbooking.ui

import com.veda.busticketbooking.db.dao.BusSeatDAO
import com.veda.busticketbooking.db.entities.BusSeatEntity
import javax.inject.Inject

class BusSeatRepository @Inject constructor(private val appDao: BusSeatDAO) {

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
}