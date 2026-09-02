package com.eric.techdocsai.document;

public record DocumentSearchResult(
		Long documentId,
		String documentTitle,
		Long chunkId,
		String content,
		Integer startPage,
		Integer endPage,
		double similarity
) {
}
