package com.veda.busticketbooking.ui

import com.veda.busticketbooking.db.entities.BusSeatEntity

interface ItemClickListener {
    fun onItemClick(seat: BusSeatEntity, position : Int)
}