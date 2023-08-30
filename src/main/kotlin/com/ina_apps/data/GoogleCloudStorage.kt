package com.ina_apps.data

import com.google.cloud.storage.Bucket
import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageClass

fun getBucketOrCreate(name: String, storage: Storage): Bucket {
    var bucket: Bucket? = storage.get(name)
    if (bucket == null) {
        val bucketInfo = BucketInfo.newBuilder(name)
            .setStorageClass(StorageClass.STANDARD)
            .setLocation("EU")
            .build()
        bucket = storage.create(bucketInfo)
    }
    return bucket!!
}