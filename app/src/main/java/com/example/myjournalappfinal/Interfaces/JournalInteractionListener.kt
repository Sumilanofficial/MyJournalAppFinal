package com.example.myjournalappfinal.Interfaces

import com.example.myjournalappfinal.Models.JournalEntry

interface JournalInteractionListener {
    fun onItemClick(journalEntry: JournalEntry)
    fun onItemLongClick(journalEntry: JournalEntry)
}