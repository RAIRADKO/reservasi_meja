package com.example.reservasi_meja.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.reservasi_meja.util.NotificationHelper

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Reservation Update"
            val body = notification.body ?: ""

            NotificationHelper(this).showNotification(title, body)
        }
    }

    override fun onNewToken(token: String) {
        // Handle token refresh if needed
    }
}