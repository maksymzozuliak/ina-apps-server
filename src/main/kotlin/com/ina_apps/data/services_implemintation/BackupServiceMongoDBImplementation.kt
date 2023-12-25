package com.ina_apps.data.services_implemintation

import com.ina_apps.model.database_classes.*
import com.ina_apps.model.services.BackupService
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList
import org.litote.kmongo.div
import org.litote.kmongo.gte
import org.litote.kmongo.lt
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter

class BackupServiceMongoDBImplementation(
    database: MongoDatabase,
    backupDatabase: MongoDatabase
) : BackupService {

    private val hourlyBackupCollection = backupDatabase.getCollection<HourlyBackup>("HourlyBackup")
    private val dailyBackupCollection = backupDatabase.getCollection<DailyBackup>("DailyBackup")

    private val ordersCollection = database.getCollection<Order>("Order")
    private val dishesCollection = database.getCollection<Dish>("Dish")
    private val userCollection = database.getCollection<User>("User")
    private val restaurantInformationCollection = database.getCollection<RestaurantInformation>("RestaurantInformation")

    override suspend fun makeHourlyBackup() {

        val currentDate = LocalDateTime.now()
        val oneHourAgo = currentDate.minusHours(1L)
        val formatter = DateTimeFormatter.ISO_INSTANT
        val startDate = oneHourAgo.atZone(ZoneId.systemDefault()).toInstant()
        val orders = ordersCollection.find(
            Order::date gte formatter.format(startDate)
        ).toList()
        hourlyBackupCollection.insertOne(
            HourlyBackup(
                timestamp = currentDate.toString(),
                orders = orders
            )
        )
    }

    override suspend fun makeDailyBackup() {

        val currentDate = LocalDateTime.now()
        val oneDayAgo = currentDate.minusDays(1L)
        val formatter = DateTimeFormatter.ISO_INSTANT
        val startDate = oneDayAgo.atZone(ZoneId.systemDefault()).toInstant()
        val orders = ordersCollection.find(
            Order::date gte formatter.format(startDate)
        ).toList()
        val users = userCollection.find(
            User::userInformation / UserInformation::dateOfRegistration gte formatter.format(startDate)
        ).toList()
        val restaurantInformation = restaurantInformationCollection.find().toList()
        val dishes = dishesCollection.find().toList()
        dailyBackupCollection.insertOne(
            DailyBackup(
                timestamp = currentDate.toString(),
                orders = orders,
                users = users,
                dishes = dishes,
                restaurantInformation = restaurantInformation
            )
        )
    }

    override suspend fun deleteOldHourlyBackup(days: Long) {

        val currentDate = LocalDateTime.now()
        val nDaysAgo = currentDate.minusDays(days)
        val formatter = DateTimeFormatter.ISO_INSTANT
        val startDate = nDaysAgo.atZone(ZoneId.systemDefault()).toInstant()
        hourlyBackupCollection.deleteMany(
            HourlyBackup::timestamp lt formatter.format(startDate)
        )
    }

    override suspend fun deleteOldDailyBackup(days: Long) {
        val currentDate = LocalDateTime.now()
        val nDaysAgo = currentDate.minusDays(days)
        val formatter = DateTimeFormatter.ISO_INSTANT
        val startDate = nDaysAgo.atZone(ZoneId.systemDefault()).toInstant()
        dailyBackupCollection.deleteMany(
            DailyBackup::timestamp lt formatter.format(startDate)
        )
    }
}
