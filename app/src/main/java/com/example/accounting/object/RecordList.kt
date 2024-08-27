package com.example.accounting.`object`

import com.example.accounting.Record

object RecordList {
    var records= mutableListOf<Record>()
    var inSum:Int=0
    var outSum:Int=0
    fun add(record:Record){
        this.records.add(record)
    }
    fun clear(){
        this.records.clear()
        this.inSum=0
        this.outSum=0
    }
}