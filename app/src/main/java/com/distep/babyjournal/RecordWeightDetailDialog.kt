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
import kotlinx.android.synthetic.main.record_weight_dialog.*
import java.text.SimpleDateFormat
import java.util.*


class RecordWeightDetailDialog(
    private val callbackListener: DialogCallbackListener<Record>
) :
    DialogFragment() {
    var record: Record? = null
    val instance = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = false
        return inflater.inflate(R.layout.record_weight_dialog, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recordCustomText = view.findViewById(R.id.record_weight_text) as EditText

        record_weight_detail_date.text = dateFormat.format(instance.time)

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
            record_weight_detail_date.text = dateFormat.format(instance.time)
        }

        record_weight_detail_date_btn.setOnClickListener {
            datePicker.show(parentFragmentManager, "datePickerShow")
        }

        record_weight_dialog_cancel.setOnClickListener {
            callbackListener.onDataReceived(ReceiveCode.Cancel())
        }

        record_weight_dialog_save.setOnClickListener {
            val text: String = recordCustomText.text.toString()

            val record = Record(instance.time, text, RecordType.WEIGHT)
            callbackListener.onDataReceived(ReceiveCode.Success(record))
        }
    }


    override fun onStart() {
        super.onStart()
    }
}
