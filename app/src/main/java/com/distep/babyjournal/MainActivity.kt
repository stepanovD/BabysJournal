package com.distep.babyjournal

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.distep.babyjournal.data.db.AppDb
import com.distep.babyjournal.data.entity.Record
import com.distep.babyjournal.data.entity.RecordType
import com.distep.babyjournal.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), DialogCallbackListener<Record> {

    @Inject
    lateinit var db: AppDb

    @Inject
    lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private var viewPager2: ViewPager2? = null
    private var recyclerView: RecyclerView? = null
    private var recordAdapter: RecordAdapter? = null

    private var pagedListLiveData: LiveData<PagedList<Record>>? = null

    private var dialogFragment: DialogFragment? = null

    private val context = this

    private val instance = Calendar.getInstance()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    private val config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(10)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance.set(Calendar.HOUR_OF_DAY, 0)
        instance.set(Calendar.MINUTE, 0)
        instance.set(Calendar.SECOND, 0)
        instance.set(Calendar.MILLISECOND, 0)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setContentView(R.layout.content_main)

        setSupportActionBar(binding.toolbar)

        binding.dateSelected.text = dateFormat.format(instance.time)

        binding.prevDateImg.setOnClickListener {
            instance.add(Calendar.HOUR_OF_DAY, -24)
            binding.dateSelected.text = dateFormat.format(instance.time)
            setAdapter(false, config, mainViewModel.recordTypeSelected.value)
        }

        binding.nextDateImg.setOnClickListener {
            instance.add(Calendar.HOUR_OF_DAY, 24)
            binding.dateSelected.text = dateFormat.format(instance.time)
            setAdapter(false, config, mainViewModel.recordTypeSelected.value)
        }


        binding.fab.setOnClickListener { view ->
            when (mainViewModel.recordTypeSelected.value) {
                RecordType.WEIGHT -> showAddWeightRecordDialog()
                else -> showAddRecordDialog()
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedRecordType = when (tab?.position) {
                    0 -> RecordType.EVENT
                    1 -> RecordType.WEIGHT
                    else -> RecordType.EVENT
                }

                mainViewModel.changeRecordType(selectedRecordType)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle tab reselect
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle tab unselect
            }
        })

        mainViewModel.recordTypeSelected.observe(this) {
            setAdapter(true, config, it)
            if(it == RecordType.EVENT) {
                binding.dateNavLayout.visibility = View.VISIBLE
            } else {
                binding.dateNavLayout.visibility = View.GONE
            }
        }

        mainViewModel.recordDelete.observe(this) {
            MaterialAlertDialogBuilder(context)
                .setTitle("Удаление записи")
                .setMessage("Вы уверены что хотите удалить запись?")
                .setNegativeButton("Отменить") { dialog, which ->
                    dialog.dismiss()
                }
                .setPositiveButton("Удалить") { dialog, which ->
                    GlobalScope.launch(Dispatchers.Main) {
                        db.recordDao().delete(it!!)
                        recordAdapter?.notifyDataSetChanged()
                    }
                }
                .show()
        }

        initRecyclerView()
    }

    private fun initRecyclerView() {
        viewPager2 = record_view_pager
        viewPager2!!.orientation = ViewPager2.ORIENTATION_VERTICAL

        val field = ViewPager2::class.java.getDeclaredField("mRecyclerView")
        field.isAccessible = true
        recyclerView = field.get(viewPager2) as RecyclerView
        recyclerView!!.clearOnChildAttachStateChangeListeners()
        recyclerView!!.layoutManager


        setAdapter(true, config, RecordType.EVENT)
    }

    private fun setAdapter(init: Boolean, config: PagedList.Config, type: RecordType?) {
        val factory: DataSource.Factory<Int, Record> = when (type) {
            RecordType.WEIGHT -> db.recordDao().getWeightItems()
            else -> db.recordDao().getItems(instance.time.time, instance.time.time + 24*60*60*1000)
        }

        pagedListLiveData?.removeObservers(this)
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .build()

        pagedListLiveData?.observe(this) { results -> recordAdapter?.submitList(results) }

        recordAdapter = RecordAdapter(mainViewModel)
        viewPager2!!.adapter = recordAdapter!!
        if (!init) {
            recyclerView!!.swapAdapter(recordAdapter!!, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddWeightRecordDialog() {
        GlobalScope.launch(Dispatchers.IO) {
            dialogFragment = RecordWeightDetailDialog(context)

            runOnUiThread {
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, dialogFragment!!)
                    .addToBackStack(null).commit()
            }
        }
    }

    private fun showAddRecordDialog() {
        GlobalScope.launch(Dispatchers.IO) {
            dialogFragment = RecordDetailDialog(context)

            (dialogFragment as RecordDetailDialog).setDate(instance)

            runOnUiThread {
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.add(android.R.id.content, dialogFragment!!)
                    .addToBackStack(null).commit()
            }
        }
    }

    override fun onDataReceived(code: ReceiveCode<Record>) {
        if (code is ReceiveCode.Success) {
            val record = code.data
            GlobalScope.launch(Dispatchers.Main) {
                val newId = db.recordDao().insert(record)
                recordAdapter?.notifyDataSetChanged()
            }
        }

        if (dialogFragment != null
            && dialogFragment!!.isVisible
        ) {
            dialogFragment!!.dismiss()
        }
    }
}