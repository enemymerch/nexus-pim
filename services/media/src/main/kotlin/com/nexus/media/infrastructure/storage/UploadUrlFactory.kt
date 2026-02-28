package com.nexus.media.infrastructure.storage

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

@Component
class UploadUrlFactory(
    @Value("\${storage.bucket:nexus-media}") private val bucket: String,
    @Value("\${storage.public-base-url:http://localhost:9000}") private val publicBaseUrl: String,
    @Value("\${storage.upload-url-ttl-seconds:900}") private val uploadUrlTtlSeconds: Long
) {

    fun createUploadUrl(storageKey: String): String {
        val encodedKey = URLEncoder.encode(storageKey, StandardCharsets.UTF_8)
        val token = UUID.randomUUID().toString()
        // Dev-friendly URL contract for MinIO/S3-compatible gateways.
        // Replace token generation with AWS SDK presigner when real signed URLs are required.
        return "${publicBaseUrl.trimEnd('/')}/${bucket.trim()}/$encodedKey?uploadToken=$token"
    }

    fun createDownloadUrl(storageKey: String): String {
        val encodedKey = URLEncoder.encode(storageKey, StandardCharsets.UTF_8)
        return "${publicBaseUrl.trimEnd('/')}/${bucket.trim()}/$encodedKey"
    }

    fun bucket(): String = bucket.trim()

    fun ttlSeconds(): Long = uploadUrlTtlSeconds
}
