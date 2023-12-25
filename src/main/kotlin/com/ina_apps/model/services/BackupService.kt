package com.ina_apps.model.services

interface BackupService {

    suspend fun makeHourlyBackup()

    suspend fun makeDailyBackup()

    suspend fun deleteOldHourlyBackup(days: Long)

    suspend fun deleteOldDailyBackup(days: Long)
}
