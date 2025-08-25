package com.therapy.nest.attachment_management.service_implementation;

import com.therapy.nest.attachment_management.dto.AttachmentDto;
import com.therapy.nest.attachment_management.entity.Attachment;
import com.therapy.nest.attachment_management.repository.AttachmentRepository;
import com.therapy.nest.attachment_management.service.AttachmentService;
import com.therapy.nest.shared.utils.CustomGeneratedData;
import com.therapy.nest.shared.utils.Response;
import com.therapy.nest.shared.utils.ResponseCode;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;


@Service
public class AttachmentServiceImpl implements AttachmentService {
    final Logger logger = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    @Value("${file.upload-dir}")
    private String UPLOADS_FOLDER;

    @Autowired
    private AttachmentRepository attachmentRepository;


    @Override
    public Response<String> getAttachmentByPath(String path) {
        try {
            Path filePath = Paths.get(UPLOADS_FOLDER).resolve(path).toAbsolutePath().normalize();
            if (!filePath.startsWith(Paths.get(UPLOADS_FOLDER).toAbsolutePath())) {
                logger.error("[GetAttachmentByPath] ❌: Access Denied on path: {}", filePath);
                return new Response<>(ResponseCode.RESTRICTED_ACCESS, false, "Access to the requested File is Denied", null);
            } else if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                logger.error("[GetAttachmentByPath] ❌: File not found or is not readable");
                return new Response<>(ResponseCode.FILE_NOT_FOUND, false, "File not found or not readable.", null);
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            String base64String = Base64.getEncoder().encodeToString(fileContent);

            return new Response<>(ResponseCode.SUCCESS, true, "", base64String);
        } catch (Exception e) {
            logger.error("[GetAttachmentByPath] ❌: Exception: {}", e.getMessage());
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to retrieve file", null);
        }
    }

    @Override
    public Response<AttachmentDto> uploadAttachment(MultipartFile file) {
        try {
            Tika tika = new Tika();
            String mimeType = tika.detect(file.getInputStream());
            String originalFilename = file.getOriginalFilename();

            if (mimeType == null || !isAllowedMimeType(mimeType)) {
                logger.error("[UploadAttachment] ❌: Mime Type {} is not supported.", mimeType);
                return new Response<>(ResponseCode.BAD_REQUEST, false, "Invalid File Type.", null);
            }

            String directoryDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path attachmentDirectory = Paths.get(UPLOADS_FOLDER + directoryDate).toAbsolutePath().normalize();
            Files.createDirectories(attachmentDirectory);

            String fileExtension = MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
            String fileName = CustomGeneratedData.GenerateUniqueID() + System.currentTimeMillis() + fileExtension;

            Path attachmentLocation = attachmentDirectory.resolve(fileName);
            Files.copy(file.getInputStream(), attachmentLocation, StandardCopyOption.REPLACE_EXISTING);

            Path basePath = Paths.get(UPLOADS_FOLDER).toAbsolutePath().normalize();
            String relativePath = basePath.relativize(attachmentLocation).toString().replace("\\", "/");

            AttachmentDto attachmentDto = new AttachmentDto();
            attachmentDto.setName(originalFilename);
            attachmentDto.setPath(relativePath);

            logger.warn("[UploadAttachment] ✅: Attachment {} uploaded successfully.", originalFilename);
            return new Response<>(ResponseCode.SUCCESS, true, "File uploaded successfully.", attachmentDto);
        } catch (Exception e) {
            logger.error("[UploadAttachment] ❌: Exception: {}", e.getMessage());
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to upload file.", null);
        }
    }

    @Override
    @Transactional
    public Response<Attachment> deleteAttachmentByPath(String path) {
        try {
            Path filePath = Paths.get(UPLOADS_FOLDER).resolve(path).toAbsolutePath().normalize();
            if (!filePath.startsWith(Paths.get(UPLOADS_FOLDER).toAbsolutePath())) {
                logger.error("[DeleteAttachmentByPath] ❌: Access Denied on path: {}", filePath);
                return new Response<>(ResponseCode.RESTRICTED_ACCESS, false, "Access to the requested File is Denied", null);
            } else if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                logger.error("[DeleteAttachmentByPath] ❌: File not found or is not readable");
                return new Response<>(ResponseCode.FILE_NOT_FOUND, false, "File not found or not readable.", null);
            }

            Attachment existAttachment = attachmentRepository.findFirstByPath(path).orElse(null);
            if (existAttachment != null) {
                attachmentRepository.delete(existAttachment);
            }

            Files.delete(filePath);

            return new Response<>(ResponseCode.SUCCESS, true, "", null);
        } catch (Exception e) {
            logger.error("[DeleteAttachmentByPath] ❌: Exception: {}", e.getMessage());
            e.printStackTrace();
            return new Response<>(ResponseCode.FAILURE, false, "Failed to delete file.", null);
        }
    }

    private boolean isAllowedMimeType(String mimeType) {
        String[] allowedMimeTypes = {
                "application/pdf",
                "image/jpeg",
                "image/png",
                "audio/mpeg",
                "audio/wav",
                "audio/aac",
                "video/mp4",
        };

        for (String allowedMimeType : allowedMimeTypes) {
            if (allowedMimeType.equals(mimeType))
                return true;
        }

        return false;
    }

}
