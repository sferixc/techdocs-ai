package com.eric.techdocsai.embedding;

import com.eric.techdocsai.document.TextChunk;

import java.util.List;

public record EmbeddedChunk(
		TextChunk chunk,
		float[] embedding
) {
}
