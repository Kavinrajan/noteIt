package com.kavin.noteit.noteit.storage

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kavin.noteit.noteit.AnkoDatabaseHelper
import com.kavin.noteit.noteit.data_model.Visitor

import org.jetbrains.anko.db.*
import org.jetbrains.anko.toast

/**
 * Created by kavinrajansm on 6/21/2018.
 */
class VisitorRepository(val context: Context) {
    val Context.database: AnkoDatabaseHelper get() = AnkoDatabaseHelper.Instance(applicationContext)
    var TAG = this.toString()

    fun findAll() : ArrayList<Visitor> = context.database.use {
        var visitor = ArrayList<Visitor>()
        try {
            select(Visitor.TABLE_NAME)
                    .parseList(object : MapRowParser<List<Visitor>> {
                        override fun parseRow(columns: Map<String, Any?>): List<Visitor> {
                            val mobile = columns.getValue("MOBILE")
                            val dob = columns.getValue("DATE")
                            val name = columns.getValue("NAME")
                            val profile_img = columns.getValue("PHOTO")
                            val problems = columns.getValue("PROBLEMS")

                            var visit = Visitor(mobile.toString(), dob.toString(), name.toString(), profile_img.toString(), problems.toString())
                            //  visit.creationDate = dateFormatter.parse(creationDate.toString())

                            visitor.add(visit)

                            return visitor
                        }
                    })

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
         visitor
    }

    fun findByMobile(mobile: String) : ArrayList<Visitor> = context.database.use {
        var visitor = ArrayList<Visitor>()
        try {
        select(Visitor.TABLE_NAME).whereSimple("(" + Visitor.MOBILE + " = "+ mobile +")")
                .parseList(object: MapRowParser<List<Visitor>>{
                    override fun parseRow(columns: Map<String, Any?>): List<Visitor> {
                        val mobile = columns.getValue("MOBILE")
                        val dob = columns.getValue("DATE")
                        val name = columns.getValue("NAME")
                        val profile_img = columns.getValue("PHOTO")
                        val problems = columns.getValue("PROBLEMS")

                        var visit = Visitor(mobile.toString(), dob.toString(),name.toString(), profile_img.toString(), problems.toString())
                        // visit.creationDate = dateFormatter.parse(creationDate.toString())

                        visitor.add(visit)

                        return visitor
                    }
                })

        } catch (e:Exception) {
            Log.e(TAG, e.toString())
        }
        visitor
    }


    fun create(visitor: Visitor) = context.database.use {
        try {
        insert(Visitor.TABLE_NAME,
                Visitor.MOBILE to visitor.mobile,
                Visitor.DATE to visitor.date,
                Visitor.NAME to visitor.name,
                Visitor.PHOTO to visitor.photo,
                Visitor.PROBLEMS to visitor.problems_doc)
        Toast.makeText(context, "Submitted successfully!!", Toast.LENGTH_LONG).show()
        } catch (e:Exception) {
            Log.e(TAG, e.toString())
        }
    }


    fun update(visitor: Visitor, prupose: String) = context.database.use {
        try {
            if(prupose.equals("submit")) {
                update(Visitor.TABLE_NAME,
                        Visitor.NAME to visitor.name,
                        Visitor.PHOTO to visitor.photo,
                        Visitor.PROBLEMS to visitor.problems_doc)
                        .whereSimple("(" + Visitor.MOBILE + " = "+ visitor.mobile +")")
                        .exec(
                        )
                Toast.makeText(context, "Documnet updated successfully!!", Toast.LENGTH_LONG).show()
            } else if(prupose.equals("profile_photo")) {

            } else {

            }
        } catch (e:Exception) {
            Log.e(TAG, e.toString())
        }
    }


    fun delete(visitor: Visitor) = context.database.use {
        try {
            delete(Visitor.TABLE_NAME, "MOBILE = {MOBILE}", "MOBILE" to visitor.mobile)
        } catch (e:Exception) {
            Log.e("TAG", e.toString())
        }
    }
}