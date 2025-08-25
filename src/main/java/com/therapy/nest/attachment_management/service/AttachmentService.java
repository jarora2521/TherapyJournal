package com.therapy.nest.attachment_management.service;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.attachment_management.entity.Attachment;
import com.therapy.nest.shared.utils.Response;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    Response<String> getAttachmentByPath(String path);

	Response<AttachmentDto> uploadAttachment(MultipartFile file);

    Response<Attachment> deleteAttachmentByPath(String path);
}
