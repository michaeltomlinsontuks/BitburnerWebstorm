package com.github.michaeltomlinsontuks.bitburnerplugin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class BitburnerNotifier(private val project: Project) {
    companion object {
        private const val GROUP_ID = "Bitburner Notifications"
    }

    fun notifyError(message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(message, NotificationType.ERROR)
            .notify(project)
    }

    fun notifyInfo(message: String) {
        if (BitburnerSettings.getInstance().showPushSuccessNotification) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(GROUP_ID)
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project)
        }
    }

    fun notifyWarning(message: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(message, NotificationType.WARNING)
            .notify(project)
    }
}
