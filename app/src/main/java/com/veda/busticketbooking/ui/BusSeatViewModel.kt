package com.veda.busticketbooking.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.veda.busticketbooking.db.entities.BusSeatEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BusSeatViewModel @Inject constructor(private val repository: BusSeatRepository): ViewModel() {

    var seatData: MutableLiveData<List<BusSeatEntity>>

    init {
        seatData = MutableLiveData()
        loadRecords()
    }

    fun getRecordsObserver(): MutableLiveData<List<BusSeatEntity>> {
        return seatData
    }

    fun loadRecords(){
        val list = repository.getRecords()
        seatData.postValue(list)
    }

    fun insertBusSeats() {
        repository.insertRecords()
        loadRecords()
    }

    fun updateBusSeat(seat: BusSeatEntity) {
        repository.updateRecords(seat)
    }

    fun reserveBusSeat(seat: BusSeatEntity) {
        repository.updateRecords(seat)
    }

}