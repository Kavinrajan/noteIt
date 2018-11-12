package com.kavin.noteit.noteit.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.graphics.PixelFormat
import android.util.Log
import android.view.*
import android.view.WindowManager
import android.view.Gravity
import android.support.constraint.ConstraintLayout

import com.kavin.noteit.noteit.*
import com.kavin.noteit.noteit.storage.VisitorRepository

// kotlin layout extensions
import kotlinx.android.synthetic.main.visitor_popup.view.*

// Anko Android - kotlin repositories
import org.jetbrains.anko.*

import org.json.JSONObject

import java.util.*


class RunningToServeAllTime() : Service(), View.OnTouchListener, View.OnClickListener {

   // var counter = 0
    // window manager
    var mWindow:WindowManager? = null
        //  layout inflatter
    var mView: View? = null
        // layout parameters
    var params:WindowManager.LayoutParams? = null
        // parent layout
    var visitor_popup: ConstraintLayout? = null

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }

    override fun onDestroy() {
        super.onCreate()
     try {
         Log.i("EXIT", "ondestroy!")
         val broadcastIntent = Intent("com.kavin.noteit.noteit.service.RestartServiceToServeAllTime")
            sendBroadcast(broadcastIntent)
            stoptimertask()
     } catch (e: Exception) {
         Log.e(this@RunningToServeAllTime.toString(), "Service destroy !" + e.toString())
        // toast(e.toString())
    }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            super.onStartCommand(intent, flags, startId)
            val mobile = intent!!.getStringExtra("mobile")
        if (intent != null) {
            if(mobile != null && !mobile.equals("")) registerPhoneOnReceiver(mobile)
        }
        startTimer()

        } catch (e: Exception) {
            Log.e(this@RunningToServeAllTime.toString(), "onStartCommand !" + e.toString())
          //  longToast(e.toString())
        }
        return START_STICKY;
    }

    private fun registerPhoneOnReceiver(mobile: String) {
        try {
            var visitor = VisitorRepository(this@RunningToServeAllTime).findByMobile(mobile)
            if (!visitor.size.equals(0)) {

                // window layout inflator
                mView = LayoutInflater.from(this@RunningToServeAllTime).inflate(R.layout.visitor_popup, null)
                // window parameter
                params = WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        PixelFormat.TRANSLUCENT
                )
                // window layout inflator reference
                    // float_btn = mView!!.findViewById(R.id.popup_float_btn) as FloatingActionButton
                visitor_popup = mView!!.visitor_popup
                val popup_name = mView!!.popup_visitor_name
                val popup_mobile = mView!!.popup_visitor_mobile
                val popup_date_visit = mView!!.popup_date_visit
                val popup_problems = mView!!.popup_problems
                val popup_photo = mView!!.visitor_pop_photo
                val popup_view = mView!!.popup_view_btn
                val popup_close = mView!!.visitor_pop_close

                val name = visitor!![0].name
                val mobile = visitor!![0].mobile
                val photo = visitor!![0].photo
                val dob = visitor!![0].date
                val problems_doc = visitor!![0].problems_doc
                val get_doc_jsnobject = JSONObject(problems_doc)
                val get_doc_jsonArray = get_doc_jsnobject.getJSONArray("problems_doc")
                var explrObject = JSONObject()
                for (i in 0 until get_doc_jsonArray.length()) {
                    explrObject = get_doc_jsonArray.getJSONObject(i)
                }
                val date_visit = explrObject.get("date_visit")
                val problems = explrObject.get("problems")

                // set image to pop_up photo
                if (!photo.equals("")) {
                    val myBitmapAgain = Common(this@RunningToServeAllTime).decodeBase64(photo)
                    popup_photo.setImageBitmap(myBitmapAgain)
                } else {
                    popup_photo.imageResource = R.drawable.ic_add_a_photo_color_24dp
                }

                popup_name.text = name
                popup_mobile.text = mobile
                popup_problems.text = "Information : " + problems
                popup_date_visit.text = "Last visited date : \n" + date_visit

                // window GGRAVITY
                params!!.gravity = Gravity.CENTER_VERTICAL        //Initially view will be added to top-left corner
                params!!.x = 0
                params!!.y = 40


                mWindow = getSystemService(Context.WINDOW_SERVICE) as WindowManager
                mWindow!!.addView(mView, params)

                // window EVENTS LISTENERS
                //   float_btn!!.setOnTouchListener(this@RunningToServeAllTime)
                popup_close!!.setOnClickListener {
                    mWindow!!.removeViewImmediate(mView)
                }

                popup_view!!.setOnClickListener {
                    mWindow!!.removeViewImmediate(mView)
                    startActivity(Intent(this, ViewVisitor::class.java).putExtra("mobile_no", mobile))
                }
              } else if (visitor.size.equals(0)) {
                    toast("Unknown number....")
              } else {

                }
        } catch (e: Exception) {
            longToast(e.toString())
        }
    }

    // handle event = window click
    override fun onClick(v: View?) {
        try {
            when(v!!.id) {
                R.id.visitor_pop_close -> {
                    if(mWindow!! != null) {
                        mWindow!!.removeView(visitor_popup)
                    }

                }
                R.id.popup_view_btn -> {
                    if(mWindow!! != null) {
                        mWindow!!.removeView(visitor_popup)
                        startActivity(Intent(this, ViewVisitor::class.java))
                    }
                }

            }
        } catch (e:Exception) {
            toast(e.toString())
        }
    }
    // handle event = window touch
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        try {
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    when(v.id) {
                    // need to work handdle minimize view
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    when(v.id) {
                    // need to work for drag
                    }
                }
                MotionEvent.ACTION_UP -> {
                    // need to work
                }
            }
        } catch (e:Exception) {
            toast(e.toString())
        }
        return false
    }

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    var oldTime: Long = 0

    fun startTimer() {
        //set a new Timer
        timer = Timer()

        //initialize the TimerTask's job
        initializeTimerTask()

        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }


     // it sets the timer to print the counter every x seconds

    fun initializeTimerTask() {
        timerTask = object : TimerTask() {
            override fun run() {
             //   Log.i("in timer", "in timer ++++  " + counter++)
            }
        }
    }

    //stop the timer, if it's not already null
    fun stoptimertask() {

        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    // declere default or predefine values
    companion object {
        // anko database reference
         val Context.database: AnkoDatabaseHelper get() =  AnkoDatabaseHelper.Instance(applicationContext)
    }

}