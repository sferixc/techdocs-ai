package com.eric.techdocsai.document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentChunkRepository extends JpaRepository<DocumentChunkEntity, Long> {

	List<DocumentChunkEntity> findByDocumentId(Long documentId);
}
