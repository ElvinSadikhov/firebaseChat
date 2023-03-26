package com.example.mychat.service

import com.example.mychat.model.MessageModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseDbService {
    private val database = Firebase.database
    val messagesRef = database.getReference("messages")

    fun addMessage(user: MessageModel) {
        messagesRef.child(messagesRef.push().key ?: "uniqueKey").setValue(user)
    }

}