package com.example.myjournalappfinal

interface JournalInteractionListener {
    fun onItemClick(journalEntry: JournalEntry)
    fun onItemLongClick(journalEntry: JournalEntry)
}
