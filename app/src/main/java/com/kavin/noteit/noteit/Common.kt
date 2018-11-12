package com.kavin.noteit.noteit

import android.content.Context
import android.database.Cursor
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Base64
import com.kavin.noteit.noteit.data_model.Visitor
import org.jetbrains.anko.db.insert
import java.io.ByteArrayOutputStream
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import java.text.SimpleDateFormat
import java.util.*


class Common(con: Context) {
    val cont = con
    var TAG = this
    class VarOrVal() {
        val alert_com = "Something went wrong. Please try again!"
    }


    fun encodeToBase64(image: Bitmap, compressFormat: Bitmap.CompressFormat, quality: Int): String {
        val byteArrayOS = ByteArrayOutputStream()
        image.compress(compressFormat, quality, byteArrayOS)
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
    }

    fun decodeBase64(input: String): Bitmap {
        val decodedBytes = Base64.decode(input, 0)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


    }

        // For common error logs

    /*fun error_log(con: Context, err: String) {
        try {

            alert(con , VarOrVal().alert_com)
            Log.d(TAG.toString() ,"Exception = : " + err)

        } catch (e:Exception) {
            com_error_alert(con)
        }
    }

    fun error_log_success(con: Context) {
        com_error_alert(con)
    }

    fun com_error_alert(con:Context): Boolean {
        Common().alert(con , VarOrVal().alert_com)
        return false
    }*/