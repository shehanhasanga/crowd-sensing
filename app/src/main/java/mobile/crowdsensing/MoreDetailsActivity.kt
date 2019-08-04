package mobile.crowdsensing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.atomic.AtomicLong


class MoreDetailsActivity : AppCompatActivity() {

    private val sampleSize = 10
    private var dataSnapshots: MutableList<DataSnapshot> = mutableListOf<DataSnapshot>()
    private var placeRef: DatabaseReference? = null
    private var eventListener: ChildEventListener? = null
    private var childrenCount = AtomicLong()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_details)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val placeId = intent.getStringExtra("PLACE_ID")

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
        for (dataSnapshot in dataSnapshots) {
//            TODO("not implemented") // Select relevant data
        }
//        TODO("not implemented") // Process selected data
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
