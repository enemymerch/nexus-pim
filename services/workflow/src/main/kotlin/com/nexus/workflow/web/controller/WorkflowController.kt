package com.nexus.workflow.web.controller

import com.nexus.workflow.application.service.WorkflowService
import com.nexus.workflow.web.dto.StartWorkflowRequest
import com.nexus.workflow.web.dto.TransitionWorkflowRequest
import com.nexus.workflow.web.dto.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/workflow/products")
class WorkflowController(
    private val workflowService: WorkflowService
) {
    @PostMapping("/{productCode}/start")
    @ResponseStatus(HttpStatus.CREATED)
    fun start(@PathVariable productCode: String, @Valid @RequestBody req: StartWorkflowRequest) =
        workflowService.start(productCode = productCode, actor = req.actor).toResponse()

    @PostMapping("/{productCode}/transitions")
    fun transition(@PathVariable productCode: String, @Valid @RequestBody req: TransitionWorkflowRequest) =
        workflowService.transition(
            productCode = productCode,
            action = req.action,
            actor = req.actor,
            comment = req.comment
        ).toResponse()

    @GetMapping("/{productCode}")
    fun get(@PathVariable productCode: String) =
        workflowService.get(productCode).toResponse()

    @GetMapping("/{productCode}/history")
    fun history(@PathVariable productCode: String) =
        workflowService.history(productCode).map { it.toResponse() }
}
