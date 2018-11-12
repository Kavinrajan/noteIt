package com.kavin.noteit.noteit

import android.net.Uri
import android.content.Context
import android.content.CursorLoader
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.Toast

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*


// Get Contacts name and Logs
class GetName {

    // Get Contacts by mobile number
     fun getContactNameByMob(phone_number: String, context: Context): String {
        var contact_name = ""
        try {
            val uri:Uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone_number))

            val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

            val cursor= context.contentResolver.query(uri, projection, null, null, null)

            if (cursor != null) {
                if (cursor!!.moveToFirst()) {
                    contact_name = cursor.getString(0)
                }
                cursor!!.close()
            } else {
                Toast.makeText(context,"No data found",Toast.LENGTH_LONG)
            }

        } catch (e: Exception) {
            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG)
        }
        return contact_name
    }

    /*// Get Contacts logs
    fun getCallLogs(context: Context, mobile:String): String {
        val cursorLoader = CursorLoader(context, CallLog.Calls.CONTENT_URI, null, null, null, null);
        val managedCursor = cursorLoader.loadInBackground();


        val  durationIndex = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        var  mob_numberIndex = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
        var  dateIndex = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        var  call_typeIndex = managedCursor.getColumnIndex(CallLog.Calls.TYPE)

        managedCursor.moveToFirst();

        val duration = managedCursor.getString(durationIndex)
        val number = managedCursor.getString(mob_numberIndex)
        val date = managedCursor.getString(dateIndex)

        val date_time = convertLongToTime(date.toLong())
        var call_details = "\n Last call Duration : " + duration + " \nDate/time : " + date_time

        return call_details
    }*/

    // Get Contacts logs
    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    fun getLastCallDetails(context: Context, phone_number: String): String {
        var call_details = ""
        val contacts = CallLog.Calls.CONTENT_URI
        try {

            val managedCursor = context.contentResolver.query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC limit 1;")
            val number = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
            val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
            val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
            val incomingtype = managedCursor.getColumnIndex(CallLog.Calls.INCOMING_TYPE.toString())

            if (managedCursor.moveToFirst()) { //  last call details
                    val callType: String
                    val phNumber = managedCursor.getString(number)
                   // val callerName = getContactName(context, phNumber)
                    if (incomingtype == -1) {
                        callType = "incoming"
                    } else {
                        callType = "outgoing"
                    }
                    val callDate = managedCursor.getString(date)
                    val  todate = convertLongToTime(callDate.toLong())
                    val callDayTime = Date(java.lang.Long.valueOf(callDate)).toString()
                    val callDuration = managedCursor.getString(duration)

                     call_details = "\n Last call Duration : " + callDuration + " \nDate/time : " + todate
                                        "\n Type : " + callType
            }
            managedCursor.close()

        } catch (e: SecurityException) {
            Log.e("Security Exception", "User denied call log permission")
        } catch (e: Exception) {
            Log.e(ViewVisitor.TAG,  e.toString())
        }
        return call_details
    }

}





