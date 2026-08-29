package com.eric.techdocsai.document;

import java.time.Instant;

public record DocumentSummaryResponse(
		Long id,
		String title,
		String author,
		DocumentFileType fileType,
		String sourcePath,
		int wordCount,
		Instant createdAt
) {
	public static DocumentSummaryResponse fromEntity(DocumentEntity document) {
		return new DocumentSummaryResponse(
				document.getId(),
				document.getTitle(),
				document.getAuthor(),
				document.getFileType(),
				document.getSourcePath(),
				document.getWordCount(),
				document.getCreatedAt()
		);
	}
}