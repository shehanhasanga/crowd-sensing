package mobile.crowdsensing

import android.app.TimePickerDialog
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.atomic.AtomicLong
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import android.graphics.Color.DKGRAY
import android.support.v4.content.ContextCompat
import android.graphics.drawable.Drawable
import com.github.mikephil.charting.utils.Utils.getSDKInt
import android.graphics.DashPathEffect
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_more_details.*
import mobile.crowdsensing.R.id.chartLight
import mobile.crowdsensing.R.id.chartTemp
import java.util.*


class MoreDetailsActivity : AppCompatActivity() {

    private val sampleSize = 10
    private var dataSnapshots: MutableList<DataSnapshot> = mutableListOf<DataSnapshot>()
    private var placeRef: DatabaseReference? = null
    private var eventListener: ChildEventListener? = null
    private var childrenCount = AtomicLong()
    private lateinit var mChart: LineChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_details)
//        progressBar1.setVisibility(View.GONE);
//        progressBar1.setVisibility(View.VISIBLE);

        progressBar1.visibility= View.GONE
        mChart = findViewById(R.id.chartLight)
        mChart.setTouchEnabled(true)
        mChart.setPinchZoom(true)
//        drawchart()


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val placeId = "ChIJO-sXwEZF4joRSEUBy8P3pHE"

        val database = FirebaseDatabase.getInstance()
        placeRef = database.getReference("$placeId")

        eventListener = placeRef
            ?.limitToLast(sampleSize)
//            ?.orderByChild("likelihood")
            ?.addChildEventListener(object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, string: String?) {
                    dataSnapshots.add(dataSnapshot)
                    processData()
                }
            })

        placeRef?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // The number of children will always be equal to 'count' since the value of
                // the dataSnapshot here will include every child_added event triggered before this point.
                childrenCount.addAndGet(dataSnapshot.childrenCount)
                processData()
            }
        })
    }

    private fun processData() {
        if(Math.min(childrenCount.get(),sampleSize.toLong()) != dataSnapshots.size.toLong()){
            return
        }
        placeRef?.removeEventListener(eventListener!!)
        val values = ArrayList<Entry>()
        var count = 0
        var mean_lux = 0.0f
        var likelihood = 0.0f
        for (dataSnapshot in dataSnapshots) {
            mean_lux = mean_lux + dataSnapshot.child("light").value.toString().toFloat() * dataSnapshot.child("likelihood").value.toString().toFloat()
            likelihood=likelihood+dataSnapshot.child("likelihood").value.toString().toFloat().toString().toFloat()
            values.add(Entry(dataSnapshot.key.toString().toFloat(), dataSnapshot.child("light").value.toString().toFloat()))
//


//            TODO("not implemented") // Select relevant data
        }
        var mean_lux_final=((mean_lux)/ ( likelihood)).toInt()
        Log.i("MapsActivity", "mean lux total  " +  mean_lux)
        Log.i("MapsActivity", "likelihood lux total  " +  likelihood)
        Log.i("MapsActivity", "likelihood lux total  " +  mean_lux_final)
        var status = getlightstatus(mean_lux_final)
        tv_status.text = status
        tv_value.text = "Average lux value is :"+mean_lux_final.toString()
        drawchart(values)
        progressBar1.setVisibility(View.GONE);


//        TODO("not implemented") // Process selected data
    }

    private fun drawchart(values:ArrayList<Entry>){
//        val values = ArrayList<Entry>()
        val xVals = ArrayList<String>()
        xVals.add("10")
        xVals.add("20")
//        values.add(Entry(1.toFloat(), 50.toFloat()))
//        values.add(Entry(5.toFloat(), 100.toFloat()))

        val set1: LineDataSet
        if (mChart.getData() != null && mChart.getData().dataSetCount > 0) {
            set1 = mChart.getData().getDataSetByIndex(0) as LineDataSet
            set1.values = values
            mChart.getData().notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            set1 = LineDataSet(values, "Lux levels")
            set1.setDrawIcons(false)
            set1.enableDashedLine(10f, 5f, 0f)
            set1.enableDashedHighlightLine(10f, 5f, 0f)
            set1.color = Color.DKGRAY
            set1.setCircleColor(Color.DKGRAY)
            set1.lineWidth = 1f
            set1.circleRadius = 3f
            set1.setDrawCircleHole(false)
            set1.valueTextSize = 9f
            set1.setDrawFilled(true)
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f
//            if (Utils.getSDKInt() >= 18) {
//                val drawable = ContextCompat.getDrawable(this, R.drawable.tooltip_frame_dark)
//                set1.fillDrawable = drawable
//            } else {
//                set1.fillColor = Color.DKGRAY
//            }
            set1.fillColor = Color.DKGRAY
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1)
            val data = LineData(dataSets)
            val xAxisFormatter = DayAxisValueFormatter(mChart)
            val xAxis = mChart.getXAxis()
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setLabelRotationAngle(-45F);
            xAxis.setValueFormatter(xAxisFormatter);
//
//            val xAxis = mChart.xAxis
//            xAxis.position = XAxis.XAxisPosition.BOTTOM
//            xAxis.setDrawGridLines(false)
//            xAxis.setValueFormatter(IAxisValueFormatter { value, axis -> xVals.get(value.toInt()) } as ValueFormatter?)
            mChart.setData(data)
            mChart.invalidate()
        }



    }
    private fun getlightstatus(luxvalue:Int): String{

        if(luxvalue > 20 && luxvalue < 50){
            return "Area with dark surroundings"

        }else if(luxvalue >50 && luxvalue < 100){
            return "Simple orientation for short visits"

        }else if(luxvalue > 100 && luxvalue < 150){
            return "visual tasks are only occasionally performed work place"

        }else if(luxvalue > 150 && luxvalue < 250){
            return "Normal Office Work"

        }else if (luxvalue > 250 && luxvalue < 500){
            return "Normal Office Work"

        }else if(luxvalue > 500 && luxvalue < 750){
            return "Like Office Landscapes"

        }else if(luxvalue > 750 && luxvalue < 1000){
            return "Normal Drawing Work place"

        }else if(luxvalue > 1000 && luxvalue < 1500){
            return "Overcast Day"

        }else if (luxvalue > 10000 && luxvalue < 11000){
            return "Fully open to Daylight"

        }else {
            return ""
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    fun getdate(view: View) {
//        val c = Calendar.getInstance()
//        val year = c.get(Calendar.YEAR)
//        val hour = c.get(Calendar.HOUR)
//        val minute = c.get(Calendar.MINUTE)
//
//        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
//
//            Toast.makeText(this, h.toString() + " : " + m +" : " , Toast.LENGTH_LONG).show()
//
//        }),hour,minute,false)
//
//        tpd.show()
//
//
//    }
}
