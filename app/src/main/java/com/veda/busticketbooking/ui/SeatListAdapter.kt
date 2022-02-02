package com.veda.busticketbooking.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.veda.busticketbooking.R
import com.veda.busticketbooking.databinding.ItemLayoutBinding
import com.veda.busticketbooking.db.entities.BusSeatEntity

class SeatListAdapter(private val dataSet: List<BusSeatEntity>, private val itemClick:ItemClickListener) :
    RecyclerView.Adapter<SeatListAdapter.ViewHolder>() {

    class ViewHolder(private val view: ItemLayoutBinding) : RecyclerView.ViewHolder(view.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: BusSeatEntity) {
            view.seat.text = item.id
            when(item.seatStatus) {
                "0" -> view.seat.setBackgroundResource(R.color.orange)
                "1" -> view.seat.setBackgroundResource(R.color.green)
                "2" -> view.seat.setBackgroundResource(R.color.blue)
                else -> view.seat.setBackgroundResource(R.color.green)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding: ItemLayoutBinding  =
            ItemLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dataModel: BusSeatEntity = dataSet[position]
        viewHolder.bind(dataModel)
        viewHolder.itemView.setOnClickListener {
            itemClick.onItemClick(dataSet[position], position) }

    }
    override fun getItemCount() = dataSet.size
}