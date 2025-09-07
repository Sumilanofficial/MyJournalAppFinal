package com.example.myjournalappfinal.Models

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize // Add this annotation to make the class Parcelable
data class JournalEntry(
    // This annotation automatically gets the document ID from Firestore
    @DocumentId var id: String = "",
    val userId: String = "",
    val title: String = "",
    val storyContent: String = "",
    val entryDate: String = "",
    val entryTime: String = "",
    val imageUrl1: String? = null,
    val imageUrl2: String? = null,
    @ServerTimestamp val timestamp: Date? = null
) : Parcelable // Implement Parcelable