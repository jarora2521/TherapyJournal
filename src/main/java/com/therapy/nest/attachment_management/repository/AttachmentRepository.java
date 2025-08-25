package com.therapy.nest.attachment_management.repository;

import com.therapy.nest.attachment_management.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
	Optional<Attachment> findFirstByUuid(@Param("uuid")String uuid);

	Optional<Attachment> findFirstByPath(@Param("path")String path);
}
