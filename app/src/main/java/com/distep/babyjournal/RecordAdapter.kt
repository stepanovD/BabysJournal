package com.distep.babyjournal

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.distep.babyjournal.data.entity.Record
import com.distep.babyjournal.data.entity.RecordType
import java.text.SimpleDateFormat


class RecordAdapter(private val viewModel: MainViewModel) :
    PagedListAdapter<Record, RecordAdapter.CouponViewHolder>(
        COUPON_COMPARATOR
    ) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val timeFormat = SimpleDateFormat("HH:mm")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val context = ContextThemeWrapper(
            parent.context,
            viewType
        )

        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.record_item_layout, parent, false)

        return CouponViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    public override fun getItem(position: Int): Record? {
        return super.getItem(position)
    }

    companion object {
        val COUPON_COMPARATOR = object : DiffUtil.ItemCallback<Record>() {
            override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean =
                oldItem.uid == newItem.uid
        }
    }

    inner class CouponViewHolder(itemView: View, val context: ContextThemeWrapper) :
        RecyclerView.ViewHolder(
            itemView
        ) {

        private val dateView: TextView = itemView.findViewById(R.id.record_date)
        private val timeView: TextView = itemView.findViewById(R.id.record_time)
        private val textView: TextView = itemView.findViewById(R.id.record_text)
        private val recordDelete: ImageView = itemView.findViewById(R.id.record_delete)

        init {
//            itemView.setOnClickListener {
//                itemClickListener(absoluteAdapterPosition)
//            }
        }

        fun bind(record: Record?) {

            if (record != null) {

                textView.text = record.event
                textView.visibility = View.VISIBLE

                when (record.type) {
                    RecordType.EVENT -> {
                        timeView.text = timeFormat.format(record.dateTime)
                        timeView.visibility = View.VISIBLE
                        dateView.visibility = View.GONE
                    }
                    RecordType.WEIGHT -> {
                        timeView.visibility = View.GONE
                        dateView.text = dateFormat.format(record.dateTime)
                        dateView.visibility = View.VISIBLE
                    }
                    else -> {
                        timeView.text = timeFormat.format(record.dateTime)
                        dateView.text = dateFormat.format(record.dateTime)
                    }
                }

                recordDelete.setOnClickListener {
                    viewModel.deleteRecord(record)
                }
            }
        }
    }
}