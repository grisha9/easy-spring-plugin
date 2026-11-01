/*
 * Copyright (c) 2024 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.spring.core.notifications

import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager

const val NOTIFICATION_GROUP_ID = "com.easy.spring.notification"

val SpringToolNotificationGroup: NotificationGroup by lazy {
    NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID)
}