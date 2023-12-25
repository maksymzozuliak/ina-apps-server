package com.ina_apps.plugins

import com.ina_apps.data.services_implemintation.BackupServiceMongoDBImplementation
import com.ina_apps.utils.CustomTimer
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.event.Level

@OptIn(DelicateCoroutinesApi::class)
fun Application.configureBackup(
    database: MongoDatabase,
    backupDatabase: MongoDatabase,
    timer: CustomTimer
) {

    val backupService = BackupServiceMongoDBImplementation(
        database, backupDatabase
    )

    timer.createNewTimer(
        24*60*60*1000L
    ) {
        GlobalScope.launch {
            backupService.makeDailyBackup()
            backupService.deleteOldDailyBackup(30L)
        }
    }

    timer.createNewTimer(
        60*60*1000L
    ) {
        GlobalScope.launch {
            backupService.makeHourlyBackup()
            backupService.deleteOldHourlyBackup(1L)
        }
    }
}
