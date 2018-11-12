package com.kavin.noteit.noteit

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.kavin.noteit.noteit.ConsultActivity.Companion.database
import com.kavin.noteit.noteit.storage.VisitorRepository
import kotlinx.android.synthetic.main.view_visitor.*
import org.jetbrains.anko.image
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.toast
import org.json.JSONObject
import com.kavin.noteit.noteit.data_model.Visitor
import com.kavin.noteit.noteit.service.RunningToServeAllTime.Companion.database
import kotlinx.coroutines.experimental.selects.select
import org.jetbrains.anko.db.classParser
import org.jetbrains.anko.db.parseList
import org.jetbrains.anko.db.rowParser
import org.jetbrains.anko.db.select
import org.jetbrains.anko.longToast


/**
 * Created by kavinrajansm on 6/21/2018.
 */
class ViewVisitor: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        try{

            super.onCreate(savedInstanceState)
            setContentView(R.layout.view_visitor)

            setSupportActionBar(toolbar)
            toolbar!!.setTitle(R.string.view_visitor)
            toolbar!!.setBackgroundColor(Color.TRANSPARENT)
            toolbar!!.setTitleTextColor(resources.getColor(R.color.colorPrimary))
            toolbar!!.setNavigationIcon(R.drawable.ic_close_primary_dark_color)
            // finish consult activity
            toolbar!!.setNavigationOnClickListener {
                finish()
            }
            val mobile_no = intent.getStringExtra("mobile_no")
            loadVisitors(mobile_no)

        }  catch(e: Exception){
            toast(e.toString())
        }
    }
    // append Visitors details.
    fun loadVisitors(mobile_param: String) {
        try {
            // READ data from SQLite DB using ANKO
            var visitor = VisitorRepository(this).findByMobile(mobile_param)
            if(!visitor.size.equals(0)) {

                val name = visitor[0].name
                val mobile = visitor[0].mobile
                val photo = visitor[0].photo
                val dob = visitor[0].date
                val problems_doc = visitor[0].problems_doc

                val get_doc_jsnobject = JSONObject(problems_doc)
                val get_doc_jsonArray = get_doc_jsnobject.getJSONArray("problems_doc")

                var explrObject = JSONObject()

                for (i in 0 until get_doc_jsonArray.length()) {
                    explrObject = get_doc_jsonArray.getJSONObject(i)
                }

                val get_date_visit = explrObject.get("date_visit")
                val date_visit_split = get_date_visit.toString().split("GMD")
                val date_visit = date_visit_split[0]
                val problems = explrObject.get("problems")

                if(!photo.equals("")) {
                    val myBitmapAgain = Common(this).decodeBase64(photo)
                    profile_photo.setImageBitmap(myBitmapAgain)
                } else {
                    profile_photo.imageResource = R.drawable.ic_add_a_photo_color_24dp
                }

                name_cus.text = name
                mobile_no.text = mobile
                dob_date.text = "DOB: " + dob
                visitor_call_logs.text =  "" + GetName().getLastCallDetails(this, "+91"+mobile)
                problems_cus.text = "Information : " + problems
                date_cus.text = "Last visited date : " + date_visit
            } else {
                toast("No record found")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
    }

    // Predefined or default variables value declerations
    companion object {
        val TAG = ViewVisitor::class.java.classes.toString()
    }
}