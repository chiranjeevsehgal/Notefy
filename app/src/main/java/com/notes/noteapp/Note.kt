package com.notes.noteapp

import io.realm.kotlin.types.RealmObject

class Note : RealmObject{
    var title: String = ""
    var description: String = ""
    var createdTime: Long = 0
}