 package com.kavin.noteit.noteit

import android.Manifest
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.content.Context

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

// model Data Class
import com.kavin.noteit.noteit.data_model.Visitor

// kotlin layout extensions
import kotlinx.android.synthetic.main.consult_ui.*

import org.jetbrains.anko.*

import java.util.*

import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.graphics.drawable.BitmapDrawable
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Build
import android.provider.Settings

import com.kavin.noteit.noteit.service.RunningToServeAllTime
import com.kavin.noteit.noteit.storage.VisitorRepository

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


 /**
  * Created by kavinrajansm on 6/20/2018.
  */

 class ConsultActivity: AppCompatActivity(), View.OnClickListener, View.OnTouchListener   {

      var pref: SharedPreferences? = null
      var editor: Editor? = null
      val constant = Constant()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.consult_ui)

            pref = getSharedPreferences(constant.sharePrefs, 0)
            editor = pref!!.edit()

            // animate views
            val durationMs = 1000L
            image_customer.alpha = 0f
            select_date.alpha = 0f
            name_txt.alpha = 0f
            submit_details.alpha = 0f

            val anim = Animate()
            anim.fadeIn(mobile_txt, durationMs)
                    .andThen(anim.fadeIn(image_customer, durationMs))
                    .andThen(anim.fadeIn(select_date, durationMs))
                    .andThen(anim.fadeIn(name_txt, durationMs))
                    .andThen(anim.fadeIn(submit_details, durationMs))
                    .subscribe()

                      // event handlers
            submit_details.setOnClickListener(this)
            image_customer.setOnClickListener(this)
            doc_img1.setOnClickListener(this)
            doc_img2.setOnClickListener(this)
            doc_img3.setOnClickListener(this)
            doc_img4.setOnClickListener(this)

            problems_txt.setOnTouchListener(this)
            select_date.setOnTouchListener(this)

            mobile_txt.addTextChangedListener(MyTextWatcher(mobile_txt!!))
            mobile_txt.addTextChangedListener(MyTextWatcher(mobile_txt_layout!!))
            select_date.addTextChangedListener(MyTextWatcher(select_date_layout!!))
            name_txt.addTextChangedListener(MyTextWatcher(name_txt_layout!!))
            problems_txt.addTextChangedListener(MyTextWatcher(problems_txt_layout!!))

            // Runtime permission
            checkAndRequestPermissions()

          //  val mCallService = RunningToServeAllTime(this)
            val mServiceIntent = Intent(this@ConsultActivity, RunningToServeAllTime::class.java)
            if (!isMyServiceRunning(RunningToServeAllTime::class.java)) {
                startService(mServiceIntent)
            }

            // other permissions (Window Overlay)
            askForSystemOverlayFunction()
            editor!!.putInt(constant.profile_photo, 0)
            editor!!.putInt(constant.visitor_doc1, 0)
            editor!!.putInt(constant.visitor_doc2, 0)
            editor!!.putInt(constant.visitor_doc3, 0)
            editor!!.putInt(constant.visitor_doc4, 0)
            editor!!.commit()

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
    }

     fun askForSystemOverlayFunction() {
         try {
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
             //If the draw over permission is not available to open the settings screen
             val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
             startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION)
         }
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
     }

     override fun onPause() {
         super.onPause()
         try {
             // To prevent starting the service if the required permission is NOT granted.
             if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
                 if (!Settings.canDrawOverlays(this)) {
                     //Permission is not available. Display error text.
                     toast("Draw over other app permission not available. Can't start the application without the permission.")
                     finish()
                 }
             } else {
                 //  super.onActivityResult(requestCode, resultCode, data)
             }
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
     }

     private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
         try {
             val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
             for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
                 if (serviceClass.name == service.service.className) {
                     Log.i("isMyServiceRunning?", true.toString() + "")
                     return true
                 }
             }
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
             Log.i("isMyServiceRunning?", false.toString() + "")
             return false
     }

     override fun onDestroy() {
         try {
             val mServiceIntent = Intent(this@ConsultActivity, RunningToServeAllTime::class.java)
             stopService(mServiceIntent)
             super.onDestroy()
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
     }
     }

    override fun onClick(v: View?) {
        try {
            val i =  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            // camera captures
            when(v!!.id) {
                R.id.submit_details -> submitDetails()
                R.id.image_customer -> startActivityForResult(i, 1)
                R.id.doc_img1 ->  startActivityForResult(i, 2)
                R.id.doc_img2 -> startActivityForResult(i, 3)
                R.id.doc_img3 -> startActivityForResult(i, 4)
                R.id.doc_img4 ->  startActivityForResult(i, 5)
            }

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)

            if(resultCode.equals(0)) {
                toast("ohhhh...!! Camera canceled")
                return
            }

            val extras = data!!.getExtras()
            val bmp = extras.get("data") as Bitmap

            when (requestCode) {
                1 -> {
                    if (resultCode === RESULT_OK) {
                        image_customer.setImageBitmap(bmp)
                        editor!!.putInt(constant.profile_photo, 1)
                        editor!!.commit()
                    }
                }
                2, 3, 4, 5 -> {
                    if (resultCode === RESULT_OK) {
                        if (requestCode.equals(2)) {
                            doc_img1.setImageBitmap(bmp)
                            editor!!.putInt(constant.visitor_doc1, 1)
                            editor!!.commit()
                        }
                        if (requestCode.equals(3)) {
                            doc_img2.setImageBitmap(bmp)
                            editor!!.putInt(constant.visitor_doc2, 1)
                            editor!!.commit()
                        }
                        if (requestCode.equals(4)) {
                            doc_img3.setImageBitmap(bmp)
                            editor!!.putInt(constant.visitor_doc3, 1)
                            editor!!.commit()
                        }
                        if (requestCode.equals(5)) {
                            doc_img4.setImageBitmap(bmp)
                            editor!!.putInt(constant.visitor_doc4, 1)
                            editor!!.commit()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        try {
            v!!.parent.requestDisallowInterceptTouchEvent(true)
            when(event!!.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
                MotionEvent.ACTION_DOWN -> {
                    when(v.id) {
                        //pick date on tuch
                        R.id.select_date ->  datePicker(v)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
        return false
    }

     // validate submit input fields
    private fun submitDetails(): Boolean {
        try {

            val mobile = mobile_txt.text.toString().trim { it <= ' ' }
            // val mobile_no_code = "+91"+mobile
            val date = select_date.text.toString().trim { it <= ' ' }
            val name = name_txt.text.toString().toUpperCase().trim { it <= ' ' }
            val problems = problems_txt.text.toString().trim { it <= ' ' }
            var profile_image = ""
            var doc_images  = ""
            var doc_image1  = ""
            var doc_image2 = ""
            var doc_image3 = ""
            var doc_image4 = ""

            val pref_photo = pref!!.getInt(constant.profile_photo, 0)
            val pref_doc1 = pref!!.getInt(constant.visitor_doc1, 0)
            val pref_doc2 = pref!!.getInt(constant.visitor_doc2, 0)
            val pref_doc3 = pref!!.getInt(constant.visitor_doc3, 0)
            val pref_doc4 = pref!!.getInt(constant.visitor_doc4, 0)

            if(pref_photo.equals(1)) {
                val get_visitor_mage = image_customer.getDrawable() as BitmapDrawable
                val tobitmap = get_visitor_mage.bitmap
                profile_image = Common(this).encodeToBase64(tobitmap, Bitmap.CompressFormat.JPEG, 100)
            }

            // convert bitmap img to base64 string If image captured
            if(pref_doc1.equals(1) ) {
                val get_doc_img1 = doc_img1.getDrawable() as BitmapDrawable
                val tobitmap = get_doc_img1.bitmap
                doc_image1 = Common(this).encodeToBase64(tobitmap, Bitmap.CompressFormat.JPEG, 100)
            }

            if(pref_doc2.equals(1)) {
                val get_doc_img2 = doc_img2.getDrawable() as BitmapDrawable
                val tobitmap = get_doc_img2.bitmap
                doc_image2 = Common(this).encodeToBase64(tobitmap, Bitmap.CompressFormat.JPEG, 100)
            }

            if(pref_doc3.equals(1)) {
                val get_doc_img3 = doc_img3.getDrawable() as BitmapDrawable
                val tobitmap = get_doc_img3.bitmap
                doc_image3 = Common(this).encodeToBase64(tobitmap, Bitmap.CompressFormat.JPEG, 100)
            }

            if(pref_doc4.equals(1)) {
                val get_doc_img4 = doc_img4.getDrawable() as BitmapDrawable
                val tobitmap = get_doc_img4.bitmap
                doc_image4 = Common(this).encodeToBase64(tobitmap, Bitmap.CompressFormat.JPEG, 100)
            }

            doc_images = doc_image1 + "?" + doc_image2 + "?" + doc_image3 + "?" + doc_image4

            if(mobile.isEmpty()) {
                 mobile_txt_layout.error = getString(R.string.empty_mobile)
                 requestFocus(mobile_txt)
                return false
            } else if(date.isEmpty()) {
                select_date_layout.error = getString(R.string.empty_date)
                requestFocus(select_date)
                return false
            } else if(name.isEmpty()) {
                name_txt_layout.error = getString(R.string.empty_name)
                requestFocus(name_txt)
                return false
            } else if(problems.isEmpty()) {
                problems_txt_layout.error = getString(R.string.empty_problems)
                requestFocus(problems_txt)
                return false
            } else {
                mobile_txt_layout.isErrorEnabled = false
                select_date_layout.isErrorEnabled = false
                name_txt_layout.isErrorEnabled = false
                problems_txt_layout.isErrorEnabled = false

                submitSuccess( mobile, date, name, problems , profile_image, doc_images)

            }
        } catch (e:Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
        return true
    }

     // validate submit input fields
    private fun submitSuccess(mobile:String, date:String, name:String, problems:String, profile_image: String, doc_images: String) {

         try {
             val system_date = Date()
             var problems_doc = ""

             var docs_obj = JSONObject()
                 docs_obj.put("date_visit", system_date)
                 docs_obj.put("problems", problems)
                 docs_obj.put("img_docs", doc_images)

                // Database Read operations
                 var visistor_list = VisitorRepository(this).findByMobile(mobile)
                 val visistor_list_size = visistor_list.size

                 if(visistor_list_size.equals(0)) {

                     var docs_to_array = JSONArray()
                     docs_to_array.put(docs_obj)

                     val  visitor_problemsObj =  JSONObject()
                        visitor_problemsObj.put("problems_doc", docs_to_array)
                        problems_doc =  visitor_problemsObj.toString()

                     // Database Insert operations
                     val insertViistor = Visitor(mobile, date, name, profile_image, problems_doc)
                     VisitorRepository(this).create(insertViistor)

                 } else {

                     val get_doc_jsnobject = JSONObject(visistor_list[0].problems_doc)
                     val get_doc_jsonArray = get_doc_jsnobject.getJSONArray("problems_doc")
                        get_doc_jsonArray.put(docs_obj)
                     val  visitor_problemsObj =  JSONObject()
                        visitor_problemsObj.put("problems_doc", get_doc_jsonArray)
                        problems_doc =  visitor_problemsObj.toString()

                     // Database Update operations
                     val updatedViistor = Visitor(mobile, date, name, profile_image, problems_doc)
                     VisitorRepository(this).update(updatedViistor, "submit")
                 }

             // RESET visitor form
             Reset( "submit")

        } catch (e: JSONException) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         } catch (e:Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
        }
    }

    // date picker
    fun datePicker(v: View?) {
        try{
            // DIALOG
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener
                { view, year, monthOfYear, dayOfMonth ->
                    var monthText = ""

                    for ((index, value) in monthArray.withIndex()) {
                        if(index == monthOfYear) monthText = value
                    }
                    // display selected date in text view
                    when(v!!.id) {
                        R.id.select_date -> {
                            select_date.setText("" + dayOfMonth + " - " + monthText + ", " + year)
                        }
                    }
                }, year, month, day)

            // SHOW
            dpd.show()
        }catch(e: Exception){
            toast(e.toString())
        }
    }

    // It's focus on current Field to enter input
    private fun requestFocus(view: View) {
        try{
            if(view.requestFocus()) {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }catch(e: Exception){
            toast(e.toString())
        }
    }

    // It's watch currently focused controls
    private inner class MyTextWatcher(private val view: View) : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            try{
                when (view.id) {
                    R.id.mobile_txt_layout -> mobile_txt_layout!!.isErrorEnabled = false
                    R.id.select_date_layout -> select_date_layout!!.isErrorEnabled = false
                    R.id.name_txt_layout -> name_txt_layout!!.isErrorEnabled = false
                    R.id.problems_txt_layout -> problems_txt_layout!!.isErrorEnabled = false
                }
            }catch(e: Exception){
                toast(e.toString())
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            try{
                when (view.id) {
                    R.id.mobile_txt -> {
                        val mobile = mobile_txt.text.toString().trim { it <= ' ' }
                        if(mobile.length == 10) {
                             val getname = GetName().getContactNameByMob(mobile, this@ConsultActivity)
                            if(!getname.equals("")) {
                                alert(getname , mobile  + "  belongs to") {
                                    positiveButton("Use it!") { name_txt.setText(getname) }
                                    negativeButton("Cancel") { name_txt.setText("") }
                                }.show()
                            }
                        }
                    }
                }
            }catch(e: Exception){
                Log.e(TAG, e.toString())
                toast(e.toString())
            }
        }

    }

     // RESET Function
    fun Reset(function: String) {
        try {
            val grey_cam : Int = R.drawable.ic_camera_alt_grey_24dp
            if(function.contentEquals("submit")) {
                mobile_txt.setText("")
                select_date.setText("")
                name_txt.setText("")
                problems_txt.setText("")
                image_customer.imageResource = R.drawable.ic_add_a_photo_color_24dp
                doc_img1.imageResource = grey_cam
                doc_img2.imageResource = grey_cam
                doc_img3.imageResource = grey_cam
                doc_img4.imageResource = grey_cam

                editor!!.putInt(constant.profile_photo, 0)
                editor!!.putInt(constant.visitor_doc1, 0)
                editor!!.putInt(constant.visitor_doc2, 0)
                editor!!.putInt(constant.visitor_doc3, 0)
                editor!!.putInt(constant.visitor_doc4, 0)
                editor!!.commit()
            }
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            toast(e.toString())
        }
    }

    // REQUEST PREMISSIONS when startup runtime
     override fun onRequestPermissionsResult(
             requestCode: Int,
             permissions: Array<String>,
             grandResults: IntArray
     ) {
         try {
             val pg = PackageManager.PERMISSION_GRANTED

             when (requestCode) {

                 REQUEST_ID_MULTIPLE_PERMISSIONS -> {

                     val perms = HashMap<String, Int>()

                     // Initialize the map with both permissions
                     perms[Manifest.permission.INTERNET] = pg
                     perms[Manifest.permission.ACCESS_NETWORK_STATE] = pg
                     perms[Manifest.permission.CAMERA] = pg
                     perms[Manifest.permission.READ_CONTACTS] = pg
                     perms[Manifest.permission.WRITE_CONTACTS] = pg
                     perms[Manifest.permission.READ_PHONE_STATE] = pg
                     perms[Manifest.permission.PROCESS_OUTGOING_CALLS] = pg
                     perms[Manifest.permission.SYSTEM_ALERT_WINDOW] = pg

                     // Fill with actual results from the user
                     if ( grandResults.size > 0 ) {

                         for(i in permissions.indices)
                             perms[permissions[i]] = grandResults[i]

                         // Check for both permissions
                         if ( perms[Manifest.permission.INTERNET] == pg
                                 && perms[Manifest.permission.ACCESS_NETWORK_STATE] == pg
                                 && perms[Manifest.permission.CAMERA] == pg
                                 && perms[Manifest.permission.WRITE_CONTACTS] == pg
                                 && perms[Manifest.permission.READ_CONTACTS] == pg
                                 && perms[Manifest.permission.READ_PHONE_STATE] == pg
                                 && perms[Manifest.permission.PROCESS_OUTGOING_CALLS] == pg
                                 && perms[Manifest.permission.SYSTEM_ALERT_WINDOW] == pg)
                         {
                             Log.d(TAG, "sms & location services permission granted")
                             // process the normal flow
                             val i = Intent(this@ConsultActivity, ConsultActivity::class.java)
                             startActivity(i)
                             finish()
                             //else any one or both the permissions are not granted
                         }
                     } else {

                         Log.d(TAG, "Some permissions are not granted ask again ")
                         // permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                         // shouldShowRequestPermissionRationale will return true
                         if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CONTACTS)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                                 || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SYSTEM_ALERT_WINDOW)) {

                             alert("Service Permissions are required for this app" , "Require permissions") {
                                 positiveButton("Ok") { checkAndRequestPermissions() }
                                 negativeButton("Cancel") { finish() }
                             }.show()

                         } else{
                             explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                         }
                     }
                     //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                 }
             }
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
     }

     private fun checkAndRequestPermissions() : Boolean {
         try {

             val internet = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.INTERNET)
             val camera = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.CAMERA)
             val access_nw_state = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.ACCESS_NETWORK_STATE)
             val write_contacts = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.WRITE_CONTACTS)
             val read_contacts = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.READ_CONTACTS)
             val read_phone_state = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.READ_PHONE_STATE)
             val process_outgoing_call = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.PROCESS_OUTGOING_CALLS)
             val system_alert_window = ContextCompat.checkSelfPermission(
                     this, Manifest.permission.SYSTEM_ALERT_WINDOW)

             val permissions = ArrayList<String>()
             val pg = PackageManager.PERMISSION_GRANTED

             if (internet != pg) {
                 permissions.add(Manifest.permission.INTERNET)
             }
             if (access_nw_state != pg) {
                 permissions.add(Manifest.permission.ACCESS_NETWORK_STATE)
             }
             if (camera != pg) {
                 permissions.add(Manifest.permission.CAMERA)
             }
             if (write_contacts != pg) {
                 permissions.add(Manifest.permission.WRITE_CONTACTS)
             }
             if (read_contacts != pg) {
                 permissions.add(Manifest.permission.READ_CONTACTS)
             }
             if (read_phone_state != pg) {
                 permissions.add(Manifest.permission.READ_PHONE_STATE)
             }
             if (process_outgoing_call != pg) {
                 permissions.add(Manifest.permission.PROCESS_OUTGOING_CALLS)
             }
             if (system_alert_window != pg) {
                 permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
             }

             if (!permissions.isEmpty()) {
                 ActivityCompat.requestPermissions(this, permissions.toTypedArray(), REQUEST_ID_MULTIPLE_PERMISSIONS)
                 return false
             }

         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
         return true
     }

    // for  peremisions extra dialogs
     private fun explain(msg: String) {
         try {
             val dialog = android.support.v7.app.AlertDialog.Builder(this)
             dialog.setMessage(msg)
                     .setPositiveButton("Yes") { paramDialogInterface, paramInt ->
                         //  permissionsclass.requestPermission(type,code)
                         startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.example.parsaniahardik.kotlin_marshmallowpermission")))

                     }
                     .setNegativeButton("Cancel") { paramDialogInterface, paramInt -> finish() }
             dialog.show()
         } catch (e: Exception) {
             Log.e(TAG, e.toString())
             toast(e.toString())
         }
     }

    // Predefined or default variables value declerations
     companion object {
            val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
            val DRAW_OVER_OTHER_APP_PERMISSION = 123
            val TAG = ConsultActivity::class.java.classes.toString()
            val Context.database: AnkoDatabaseHelper get() =  AnkoDatabaseHelper.Instance(applicationContext)
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val monthArray = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec")
    }

}