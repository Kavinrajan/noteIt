package com.kavin.noteit.noteit.service


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import org.jetbrains.anko.toast
import android.telephony.TelephonyManager
import android.support.v4.app.NotificationCompat.getExtras





/**
 * Created by kavinrajansm on 6/18/2018.
 */
class IncommingCallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            // outgoing call mobile number
           // val mobile_number = intent!!.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
             var mobile_number = ""
            if (intent!!.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
                mobile_number = intent!!.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            } else {
                val stateStr = intent.extras.getString(TelephonyManager.EXTRA_STATE)
                if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    mobile_number = intent.extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)
                } else {
                    mobile_number = ""
                }
            }

            /*if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                Toast.makeText(context, "Phone Is Ringing", Toast.LENGTH_LONG).show()
            }*/

            // add or leave country code
            var code_mobile_number = mobile_number

            if(!mobile_number.length.equals(10)) {
                if(mobile_number.length.equals(13)) {
                    val seperate_mobile = mobile_number.substring(3)
                    code_mobile_number = seperate_mobile
                }
            }

            // if(!mobile_number.equals("")) {
            if(mobile_number != null && !mobile_number.trim().isEmpty()) {

                // start service
                val mServiceIntent = Intent(context, RunningToServeAllTime::class.java)
                mServiceIntent.putExtra("mobile", code_mobile_number)
                context!!.startService(mServiceIntent)
            }

        } catch (e: Exception) {
            Log.i(this@IncommingCallReceiver.toString(), "Receiver Service Stops!")
           // Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
        }
    }
}


