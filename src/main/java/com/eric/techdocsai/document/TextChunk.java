package com.eric.techdocsai.document;

public record TextChunk(
		int chunkIndex,
		String content,
		int startPage,
		int endPage,
		int wordCount
) {}