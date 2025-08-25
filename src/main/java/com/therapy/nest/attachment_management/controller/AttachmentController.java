package com.therapy.nest.attachment_management.controller;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.attachment_management.entity.Attachment;
import com.therapy.nest.attachment_management.service.AttachmentService;
import com.therapy.nest.shared.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @PreAuthorize("hasAuthority('ROLE_VIEW_ATTACHMENT')")
    @GetMapping
    public Response<String> getAttachment(@RequestParam("path") String path) {
        return attachmentService.getAttachmentByPath(path);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_CREATE_ATTACHMENT', 'ROLE_EDIT_ATTACHMENT')")
    @PostMapping
    public Response<AttachmentDto> uploadAttachment(@RequestParam("file") MultipartFile file) {
        return attachmentService.uploadAttachment(file);
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE_ATTACHMENT')")
    @PostMapping("/delete")
    public Response<Attachment> deleteAttachmentByUuid(@RequestParam("path") String path) {
        return attachmentService.deleteAttachmentByPath(path);
    }
}
