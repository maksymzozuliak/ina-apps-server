package com.ina_apps.data

import com.google.cloud.storage.*

fun getBucketOrCreate(name: String, storage: Storage): Bucket {
    var bucket: Bucket? = storage.get(name)
    if (bucket == null) {
        val bucketInfo = BucketInfo.newBuilder(name)
            .setStorageClass(StorageClass.STANDARD)
            .setLocation("EU")
            .build()
        bucket = storage.create(bucketInfo)
        storage.updateAcl(name, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
    }
    return bucket!!
}
