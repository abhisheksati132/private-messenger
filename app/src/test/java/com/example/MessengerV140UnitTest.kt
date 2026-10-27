package com.example

import com.example.data.crypto.CryptoHelper
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.model.MessageStatus
import com.example.data.model.MessageType
import com.example.ui.screens.formatFileSize
import com.example.util.AppUpdateManager
import org.junit.Assert.*
import org.junit.Test

class MessengerV140UnitTest {

    @Test
    fun testMessageEntity_withV140Fields() {
        val message = MessageEntity(
            id = 100L,
            conversationId = 1L,
            senderId = "me",
            senderName = "You",
            text = "quarterly_report.pdf",
            timestamp = System.currentTimeMillis(),
            isMe = true,
            status = MessageStatus.SENT,
            type = MessageType.DOCUMENT,
            isPinned = true,
            isEdited = false,
            fileName = "quarterly_report.pdf",
            fileSizeBytes = 2_450_000L
        )

        assertTrue(message.isPinned)
        assertFalse(message.isEdited)
        assertEquals(MessageType.DOCUMENT, message.type)
        assertEquals("quarterly_report.pdf", message.fileName)
        assertEquals(2_450_000L, message.fileSizeBytes)
    }

    @Test
    fun testEditedMessage_encryptionAndDecryption() {
        val originalText = "Meet me at 5 PM"
        val cipher = CryptoHelper.encrypt(originalText)
        val decrypted = CryptoHelper.decrypt(cipher)
        assertEquals(originalText, decrypted)

        // Edit message
        val editedText = "Meet me at 6:30 PM (Updated)"
        val editedCipher = CryptoHelper.encrypt(editedText)
        val editedDecrypted = CryptoHelper.decrypt(editedCipher)
        assertEquals(editedText, editedDecrypted)
        assertNotEquals(cipher, editedCipher)
    }

    @Test
    fun testDynamicIV_producesDifferentCiphertextForSamePlaintext() {
        val text = "Top Secret Transmission"
        val cipher1 = CryptoHelper.encrypt(text)
        val cipher2 = CryptoHelper.encrypt(text)

        // With fresh 12-byte random IV per encryption, ciphertexts must NEVER be identical
        assertNotEquals(cipher1, cipher2)

        // Both must decrypt cleanly back to the identical plaintext
        assertEquals(text, CryptoHelper.decrypt(cipher1))
        assertEquals(text, CryptoHelper.decrypt(cipher2))
    }

    @Test
    fun testPBKDF2_PinHashingAndVerification() {
        val pin = "7492"
        val hashed = CryptoHelper.hashPin(pin)

        assertFalse(hashed == pin)
        assertTrue(hashed.length >= 32)
        assertTrue(CryptoHelper.verifyPin("7492", hashed))
        assertFalse(CryptoHelper.verifyPin("0000", hashed))
        assertFalse(CryptoHelper.verifyPin("1234", hashed))
    }

    @Test
    fun testFormatFileSize() {
        assertEquals("0 B", formatFileSize(0L))
        assertEquals("500.0 B", formatFileSize(500L))
        assertEquals("1.0 KB", formatFileSize(1024L))
        assertEquals("2.5 MB", formatFileSize(2_621_440L))
        assertEquals("1.0 GB", formatFileSize(1_073_741_824L))
    }

    @Test
    fun testAudioPlaybackSpeedCycle() {
        var currentSpeed = 1.0f

        fun getNextSpeed(speed: Float): Float {
            return when {
                speed < 1.25f -> 1.5f
                speed < 1.75f -> 2.0f
                else -> 1.0f
            }
        }

        currentSpeed = getNextSpeed(currentSpeed)
        assertEquals(1.5f, currentSpeed, 0.01f)

        currentSpeed = getNextSpeed(currentSpeed)
        assertEquals(2.0f, currentSpeed, 0.01f)

        currentSpeed = getNextSpeed(currentSpeed)
        assertEquals(1.0f, currentSpeed, 0.01f)
    }

    @Test
    fun testSemanticVersionComparison_inAppUpdater() {
        assertTrue(AppUpdateManager.isNewerVersion("v1.6.0", "1.5.1"))
        assertTrue(AppUpdateManager.isNewerVersion("1.6.1", "1.6.0"))
        assertTrue(AppUpdateManager.isNewerVersion("v2.0.0", "1.6.0"))
        assertTrue(AppUpdateManager.isNewerVersion("1.10.0", "1.9.5"))

        assertFalse(AppUpdateManager.isNewerVersion("v1.5.1", "1.6.0"))
        assertFalse(AppUpdateManager.isNewerVersion("1.6.0", "1.6.0"))
        assertFalse(AppUpdateManager.isNewerVersion("v1.6.0", "v1.6.0"))
        assertFalse(AppUpdateManager.isNewerVersion("1.6.0-beta", "1.6.0"))
        assertFalse(AppUpdateManager.isNewerVersion("", "1.6.0"))
    }

    @Test
    fun testTamperedCiphertext_recoversGracefullyWithoutCrash() {
        val original = "Sensitive financial payload"
        val cipher = CryptoHelper.encrypt(original)

        // Corrupt several bytes in the middle of ciphertext to simulate bit-flip / tamper
        val cipherBytes = CryptoHelper.base64Decode(cipher)
        cipherBytes[cipherBytes.size / 2] = (cipherBytes[cipherBytes.size / 2] + 1).toByte()
        val tamperedCipher = CryptoHelper.base64Encode(cipherBytes)

        val result = CryptoHelper.decrypt(tamperedCipher)
        // Authenticated AES-GCM tag verification must reject tampered bytes safely
        assertEquals("[Encrypted Message]", result)
    }

    @Test
    fun testCorruptBase64_decryptsSafelyWithoutCrash() {
        val garbage = "NotAValidBase64String%%%$$$"
        val result = CryptoHelper.decrypt(garbage)
        assertEquals("[Encrypted Message]", result)
    }

    @Test
    fun testEmptyAndBlankStrings_encryptionSafety() {
        assertEquals("", CryptoHelper.encrypt(""))
        assertEquals("", CryptoHelper.decrypt(""))
    }

    @Test
    fun testLargePayload_encryptDecryptCycle() {
        val largeText = "A".repeat(50_000) + "B".repeat(50_000)
        val encrypted = CryptoHelper.encrypt(largeText)
        val decrypted = CryptoHelper.decrypt(encrypted)
        assertEquals(largeText, decrypted)
    }

    @Test
    fun testConversationEntity_metadata() {
        val conv = ConversationEntity(
            id = 5L,
            peerId = "u_sarah",
            peerName = "Sarah Connor",
            peerHandle = "sarah.99",
            lastMessage = "Status update",
            lastTimestamp = 1700000000L,
            unreadCount = 3,
            isOnline = true,
            isEncrypted = true,
            keyFingerprint = "7A:B3:9F",
            disappearingTimerSeconds = 86400L
        )

        assertEquals("sarah.99", conv.peerHandle)
        assertEquals(3, conv.unreadCount)
        assertTrue(conv.isEncrypted)
        assertEquals(86400L, conv.disappearingTimerSeconds)
        assertEquals("7A:B3:9F", conv.keyFingerprint)
    }
}
