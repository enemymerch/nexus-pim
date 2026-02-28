package com.nexus.media.web.controller

import com.nexus.media.application.service.MediaService
import com.nexus.media.web.dto.CompleteUploadRequest
import com.nexus.media.web.dto.InitiateUploadRequest
import com.nexus.media.web.dto.UpdatePresentationRequest
import com.nexus.media.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/media/assets")
class MediaController(
    private val mediaService: MediaService
) {

    @PostMapping("/uploads")
    @ResponseStatus(HttpStatus.CREATED)
    fun initiateUpload(@Valid @RequestBody req: InitiateUploadRequest) =
        mediaService.initiateUpload(
            productCode = req.productCode,
            variantCode = req.variantCode,
            mediaType = req.mediaType,
            role = req.role,
            mimeType = req.mimeType,
            originalFilename = req.originalFilename,
            sizeBytes = req.sizeBytes,
            locale = req.locale,
            channel = req.channel,
            sortOrder = req.sortOrder
        ).toResponse()

    @PostMapping("/{assetId}/complete")
    fun completeUpload(
        @PathVariable assetId: UUID,
        @Valid @RequestBody req: CompleteUploadRequest
    ) = mediaService.completeUpload(
        assetId = assetId,
        sizeBytes = req.sizeBytes,
        checksumSha256 = req.checksumSha256
    ).toResponse()

    @PatchMapping("/{assetId}/presentation")
    fun updatePresentation(
        @PathVariable assetId: UUID,
        @Valid @RequestBody req: UpdatePresentationRequest
    ) = mediaService.updatePresentation(
        assetId = assetId,
        role = req.role,
        locale = req.locale,
        channel = req.channel,
        sortOrder = req.sortOrder
    ).toResponse()

    @GetMapping("/{assetId}")
    fun getAsset(@PathVariable assetId: UUID) =
        mediaService.getAsset(assetId).toResponse()

    @GetMapping
    fun listByProduct(@RequestParam productCode: String) =
        mediaService.listByProduct(productCode).map { it.toResponse() }

    @DeleteMapping("/{assetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteAsset(@PathVariable assetId: UUID) {
        mediaService.deleteAsset(assetId)
    }
}
