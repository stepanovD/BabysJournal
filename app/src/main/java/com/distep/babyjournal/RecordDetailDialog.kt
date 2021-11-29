package com.distep.babyjournal

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import com.distep.babyjournal.data.entity.Record
import com.distep.babyjournal.data.entity.RecordType
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.record_dialog.*
import java.text.SimpleDateFormat
import java.util.*


class RecordDetailDialog(
    private val callbackListener: DialogCallbackListener<Record>
) :
    DialogFragment() {
    var record: Record? = null
    private val instance = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val timeFormat = SimpleDateFormat("HH:mm")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.record_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        instance.timeZone =
        val levels = resources.getStringArray(R.array.events)

        val recordRadioText = view.findViewById(R.id.record_radio_text) as RadioGroup
        val recordCustomText = view.findViewById(R.id.record_custom_text) as EditText

        for (i in levels.indices) {
            val rdbtn = RadioButton(activity)
            rdbtn.id = i
            rdbtn.text = levels[i]
            recordRadioText.addView(rdbtn)
        }

        val rdbtn = RadioButton(activity)
        rdbtn.id = levels.size
        rdbtn.text = "Другое"
        recordRadioText.addView(rdbtn)

        recordRadioText.check(0)

        record_detail_date.text = dateFormat.format(instance.time)
        record_detail_time.text = timeFormat.format(instance.time)

        val timePicker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(instance.get(Calendar.HOUR_OF_DAY))
                .setMinute(instance.get(Calendar.MINUTE))
                .setInputMode(INPUT_MODE_KEYBOARD)
                .setTitleText("Время события")
                .build()

        timePicker.addOnPositiveButtonClickListener {
            instance.set(Calendar.MINUTE, timePicker.minute)
            instance.set(Calendar.HOUR_OF_DAY, timePicker.hour)

            record_detail_time.text = timeFormat.format(instance.time)
        }

        record_detail_time_btn.setOnClickListener {
            timePicker.show(parentFragmentManager, "timePickerShow")
        }

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Дата")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val build = Calendar.getInstance()
            build.timeInMillis = datePicker.selection!!

            instance.set(Calendar.YEAR, build.get(Calendar.YEAR))
            instance.set(Calendar.DAY_OF_YEAR, build.get(Calendar.DAY_OF_YEAR))
            record_detail_date.text = dateFormat.format(instance.time)
        }

        record_detail_date_btn.setOnClickListener {
            datePicker.show(parentFragmentManager, "timePickerShow")
        }

        record_dialog_cancel.setOnClickListener {
            callbackListener.onDataReceived(ReceiveCode.Cancel())
        }

        record_dialog_save.setOnClickListener {
            val checkedRadioButtonId = recordRadioText.checkedRadioButtonId
            val text: String = if (checkedRadioButtonId < levels.size)
                levels[checkedRadioButtonId]
            else
                recordCustomText.text.toString()

            val record = Record(instance.time, text, RecordType.EVENT)
            callbackListener.onDataReceived(ReceiveCode.Success(record))
        }
    }

    fun setDate(inst: Calendar) {
        instance.set(Calendar.YEAR, inst.get(Calendar.YEAR))
        instance.set(Calendar.DAY_OF_YEAR, inst.get(Calendar.DAY_OF_YEAR))
    }

    override fun onStart() {
        super.onStart()
    }
}
