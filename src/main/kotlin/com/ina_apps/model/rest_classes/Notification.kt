package com.ina_apps.model.rest_classes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
//    @SerialName("include_external_user_ids")
//    val includeExternalUserIds: List<String>? = null,
    @SerialName("included_segments")
    val includedSegments: List<String>?,
    val contents: NotificationMessage,
    val headings: NotificationMessage,
    @SerialName("app_id")
    val appId: String? = null,
//    @SerialName("ios_attachments")
//    val iosAttachments: String? = null,
    @SerialName("big_picture")
    val bigPicture: String? = null,
//    val ttl: Int? = null,
//    @SerialName("send_after")
//    val sendAfter: String? = null
)