package com.yuukaze.i18next.service;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class Notifier {
    static String ID = "EasyI18n Notification Group";

    public static void notifySuccess(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(ID)
                .createNotification(content, NotificationType.INFORMATION)
                .notify(project);
    }
    public static void notifyError(@Nullable Project project, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(ID)
                .createNotification(content, NotificationType.ERROR)
                .notify(project);
    }
}
