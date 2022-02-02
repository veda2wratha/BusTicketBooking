package com.veda.busticketbooking.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.veda.busticketbooking.db.dao.BusSeatDAO
import com.veda.busticketbooking.db.entities.BusSeatEntity

@Database(entities = [BusSeatEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getBusSeatDAO(): BusSeatDAO
    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getAppDB(context: Context): AppDatabase {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext, AppDatabase::class.java, "BusTicketBooking")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }
    }
}