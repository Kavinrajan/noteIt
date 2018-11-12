package com.kavin.noteit.noteit.data_model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import java.util.*

@SuppressLint("ParcelCreator")
data class Visitor(val mobile:String, val date:String, val name:String, val photo:String, val problems_doc:String): Parcelable {

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

  //  var creationDate: Date = Date()
    companion object {

        val TABLE_NAME = "CUSTOMERS"
        val MOBILE = "MOBILE"
        val DATE= "DATE"
        val NAME = "NAME"
        val PHOTO = "PHOTO"
        val PROBLEMS = "PROBLEMS"
    }

}