package com.example.data.remote

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.crypto.CryptoHelper
import com.example.data.local.AppDatabase
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val TAG = "FCMService"
        const val CHANNEL_ID = "vault_messenger_channel"
        const val CHANNEL_NAME = "Encrypted Messages & Calls"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM Token received: $token")
        val prefs = applicationContext.getSharedPreferences("vault_messenger_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "FCM Message from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val senderHandle = data["senderHandle"] ?: ""
        val cipherText = data["cipherText"] ?: ""
        val typeStr = data["type"] ?: "TEXT"
        val timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
        val docId = data["docId"] ?: data["cloudMsgDocId"]

        // If payload contains an encrypted message, persist immediately to Room
        if (senderHandle.isNotBlank() && cipherText.isNotBlank()) {
            serviceScope.launch {
                try {
                    val db = AppDatabase.getDatabase(applicationContext, serviceScope)
                    val dao = db.messengerDao()

                    val type = try { MessageType.valueOf(typeStr) } catch (e: Exception) { MessageType.TEXT }
                    val decrypted = if (type == MessageType.TEXT) CryptoHelper.decrypt(cipherText) else cipherText

                    var conv = dao.getConversationByHandle(senderHandle)
                    val conversationId: Long
                    val preview = when (type) {
                        MessageType.IMAGE -> "📷 Photo attachment"
                        MessageType.VOICE -> "🎤 Voice note"
                        MessageType.DOCUMENT -> "📁 Document"
                        else -> decrypted
                    }

                    if (conv == null) {
                        val newConv = ConversationEntity(
                            peerId = "u_${senderHandle.replace(".", "_")}",
                            peerName = "@$senderHandle",
                            peerHandle = senderHandle,
                            avatarBgColorHex = "#EADDFF",
                            avatarTextColorHex = "#21005D",
                            lastMessage = preview,
                            lastTimestamp = timestamp,
                            unreadCount = 1,
                            isOnline = false,
                            isEncrypted = true,
                            keyFingerprint = CryptoHelper.generateFingerprint(senderHandle)
                        )
                        conversationId = dao.insertConversation(newConv)
                    } else {
                        conversationId = conv.id
                        dao.updateConversation(
                            conv.copy(
                                lastMessage = preview,
                                lastTimestamp = timestamp,
                                unreadCount = conv.unreadCount + 1
                            )
                        )
                    }

                    val msg = MessageEntity(
                        conversationId = conversationId,
                        senderId = senderHandle,
                        senderName = "@$senderHandle",
                        text = decrypted,
                        cipherText = cipherText,
                        timestamp = timestamp,
                        isMe = false,
                        status = MessageStatus.DELIVERED,
                        type = type,
                        cloudMsgDocId = docId
                    )
                    dao.insertMessage(msg)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed background message persistence: ${e.message}")
                }
            }
        }

        val title = data["title"] ?: remoteMessage.notification?.title ?: "Encrypted Message"
        val body = data["body"] ?: remoteMessage.notification?.body ?: "New incoming secure transmission"

        showNotification(title, body, senderHandle)
    }

    private fun showNotification(title: String, message: String, senderHandle: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority notifications for secure chats and calls"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("sender_handle", senderHandle)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
