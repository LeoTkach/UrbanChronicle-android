package com.leotkach.urbanchronicle

import android.app.Application
import com.leotkach.urbanchronicle.data.AppDatabase
import com.leotkach.urbanchronicle.data.ChronicleRepository
import com.leotkach.urbanchronicle.data.SessionStore
import com.leotkach.urbanchronicle.data.ensureSeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class UrbanChronicleApplication : Application() {
    lateinit var repository: ChronicleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        val session = SessionStore(this)
        repository = ChronicleRepository(db, session)
        runBlocking(Dispatchers.IO) {
            db.ensureSeeded()
        }
    }
}
