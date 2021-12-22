package com.example.android.politicalpreparedness.network.jsonadapter

import com.example.android.politicalpreparedness.network.models.Division
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.SimpleDateFormat
import java.util.*

class ElectionAdapter {
    @FromJson
    fun divisionFromJson (ocdDivisionId: String): Division {
        val countryDelimiter = "country:"
        val stateDelimiter = "state:"
        val country = ocdDivisionId.substringAfter(countryDelimiter,"")
                .substringBefore("/")
        val state = ocdDivisionId.substringAfter(stateDelimiter,"")
                .substringBefore("/")
        return Division(ocdDivisionId, country, state)
    }

    @ToJson
    fun divisionToJson (division: Division): String {
        return division.id
    }
}

class DateAdapter {
    val dateFormat =  "yyyy-MM-dd"

    @FromJson
    fun stringToDate(electionDay: String): Date {
        return SimpleDateFormat(dateFormat).parse(electionDay)
    }

    @ToJson
    fun dateToString(date: Date): String {
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        return dateFormatter.format(date)
    }
}