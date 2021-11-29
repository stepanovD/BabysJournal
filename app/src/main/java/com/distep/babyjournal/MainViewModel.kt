package com.distep.babyjournal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.distep.babyjournal.data.db.AppDb
import com.distep.babyjournal.data.entity.Record
import com.distep.babyjournal.data.entity.RecordType
import javax.inject.Inject

class MainViewModel @Inject constructor(
    var db: AppDb
) : ViewModel() {

    private val _recordTypeSelected = MutableLiveData(RecordType.EVENT)
    val recordTypeSelected: LiveData<RecordType?> = _recordTypeSelected

    private val _recordDelete = MutableLiveData<Record?>()
    val recordDelete: LiveData<Record?> = _recordDelete

    fun changeRecordType(type: RecordType) {
        _recordTypeSelected.value = type
    }

    fun deleteRecord(record: Record) {
        _recordDelete.value = record
    }
}